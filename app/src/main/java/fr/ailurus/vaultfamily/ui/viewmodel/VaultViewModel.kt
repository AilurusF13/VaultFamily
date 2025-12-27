package fr.ailurus.vaultfamily.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.ailurus.vaultfamily.data.model.*
import fr.ailurus.vaultfamily.data.repository.VaultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VaultUiState(
    val entries: List<Entry> = emptyList(),
    val groups: List<Group> = emptyList(),
    val searchQuery: String = "",
    val groupQuery: Long = 0L
)

class VaultViewModel(private val repository: VaultRepository) : ViewModel() {

    // Les 'MutableStateFlow' pour les requêtes de 
    private val _searchQuery = MutableStateFlow("")
    private val _groupQuery = MutableStateFlow(0L)

    // On expose les StateFlow directement à partir du combine
    val uiState: StateFlow<VaultUiState> = combine(
        repository.getAllEntries(), // On utilise directement le Flow du repository
        repository.getAllGroups(),
        _searchQuery,
        _groupQuery
    ) { allEntries, allGroups, searchQuery, groupQuery ->
        val filteredEntries = if (searchQuery.isBlank() && groupQuery == 0L) {
            allEntries // pas de filtrage si les requêtes sont vides
        } else {
            allEntries.filter { entry ->
                val matchesSearch = searchQuery.isBlank() || entry.entrySite.contains(searchQuery, ignoreCase = true)
                val matchesGroup = groupQuery == 0L || entry.groupId == groupQuery
                matchesSearch && matchesGroup
            }
        }

        VaultUiState(
            entries = filteredEntries,
            groups = allGroups,
            searchQuery = searchQuery,
            groupQuery = groupQuery
        )
    }.stateIn( // On transforme le Flow résultant en un StateFlow
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = VaultUiState() // État initial pendant que le Flow se met en place
    )

    // sauvegérder et supprimer les entrées
    fun saveEntry(entry: Entry, secret: EntrySecret){
        viewModelScope.launch {
            repository.saveEntry(entry, secret)
        }
    }

    fun deleteEntry(entry: Entry){
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    // ajouter ou supprimer un groupe
    fun saveGroup(group: Group, secret: GroupSecret){
        viewModelScope.launch {
            repository.saveGroup(group, secret)
        }
    }
    
    fun deleteGroup(group: Group){
        viewModelScope.launch {
            repository.deleteGroup(group)
        }
    }
    
    // Setters des filtres
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
    fun onGroupQueryChange(newGroup: Long) {
        _groupQuery.value = newGroup
    }
    companion object {
        fun Factory(repository: VaultRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                VaultViewModel(repository)
            }
        }
    }
}
