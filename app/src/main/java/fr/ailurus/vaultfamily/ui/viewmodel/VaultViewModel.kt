package fr.ailurus.vaultfamily.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.ailurus.vaultfamily.data.model.Entry
import fr.ailurus.vaultfamily.data.repository.VaultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class VaultUiState(
    val entries: List<Entry> = emptyList(),
    val searchQuery: String = "",
    val groupQuery: String = ""
)

class VaultViewModel(private val repository: VaultRepository) : ViewModel() {

    // Les 'MutableStateFlow' pour les requêtes de recherche et de groupe
    private val _searchQuery = MutableStateFlow("")
    private val _groupQuery = MutableStateFlow("")

    // On expose les StateFlow directement à partir du combine
    val uiState: StateFlow<VaultUiState> = combine(
        repository.getAllEntries(), // On utilise directement le Flow du repository
        _searchQuery,
        _groupQuery
    ) { allEntries, search, group ->
        val filteredEntries = if (search.isBlank() && group.isBlank()) {
            allEntries // Optimisation : pas de filtrage si les requêtes sont vides
        } else {
            allEntries.filter { entry ->
                val matchesSearch = search.isBlank() || entry.siteWeb.contains(search, ignoreCase = true)
                val matchesGroup = group.isBlank() || entry.group.equals(group, ignoreCase = true)
                matchesSearch && matchesGroup
            }
        }

        VaultUiState(
            entries = filteredEntries,
            searchQuery = search,
            groupQuery = group
        )
    }.stateIn( // On transforme le Flow résultant en un StateFlow
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1), // Le Flow reste actif 1s après que l'UI ne l'écoute plus
        initialValue = VaultUiState() // État initial pendant que le Flow se met en place
    )

    // Setters des filtres
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onGroupQueryChange(newGroup: String) {
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
