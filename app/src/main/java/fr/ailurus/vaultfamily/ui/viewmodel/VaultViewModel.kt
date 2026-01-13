package fr.ailurus.vaultfamily.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.ailurus.vaultfamily.data.model.*
import fr.ailurus.vaultfamily.data.model.uistate.VaultUiState
import fr.ailurus.vaultfamily.data.network.*
import fr.ailurus.vaultfamily.data.repository.VaultRepository
import fr.ailurus.vaultfamily.domain.network.DiscoveryManagerImpl
import fr.ailurus.vaultfamily.domain.network.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaultViewModel(private val repository: VaultRepository, context: Context) : ViewModel() {

    // On garde les queries pour le filtrage
    private val _searchQuery = MutableStateFlow("")
    private val _groupQuery = MutableStateFlow(0L)

    // L'état complet de l'écran (Données + UI)
    val uiState: StateFlow<VaultUiState> = combine(
        repository.getAllEntries(), // Flux des entrées (DB)
        repository.getAllGroups(),  // Flux des groupes (DB)
        _searchQuery,               // Recherche texte
        _groupQuery                 // Filtre par ID de groupe
    ) { allEntries, allGroups, searchQuery, groupQuery ->

        // Logique de filtrage (Calculée à chaque changement d'un des 4 flux)
        val filteredEntries = allEntries.filter { entry ->
            val matchesSearch = searchQuery.isBlank() ||
                    entry.entrySite.contains(searchQuery, ignoreCase = true)
            val matchesGroup = groupQuery == 0L || entry.groupId == groupQuery
            matchesSearch && matchesGroup
        }

        // On retourne l'objet d'état pour la vue
        VaultUiState(
            entries = filteredEntries,
            groups = allGroups,
            searchQuery = searchQuery,
            groupQuery = groupQuery
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VaultUiState()
    )

    private val _lastMessage = MutableStateFlow("Aucun message")
    val lastMessage: StateFlow<String> = _lastMessage.asStateFlow()

    private val messagePort = 1331
    private val networkManager = NetworkManager()
    private val discoveryManager: DiscoveryManager = DiscoveryManagerImpl(context)

    // Transformation du Set en List pour l'UI
    val ips: StateFlow<List<String>> = discoveryManager.discoveredDevices
        .map { it.toList() } // Plus simple que combine
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // 1. Démarrage des services réseau
        val safeDeviceName = "Vault_${android.os.Build.MODEL.replace(" ", "_")}"

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("VaultViewModel", "Lancement NSD avec le nom : $safeDeviceName")

            discoveryManager.startDiscovery(
                serviceName = safeDeviceName,
                port = messagePort
            )

            networkManager.startServer(messagePort) { data ->
                _lastMessage.value = String(data)
            }
        }

        // 2. Boucle de nettoyage (Cleanup)
        // Cette coroutine s'arrêtera automatiquement quand le ViewModel sera détruit
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(10000) // On vérifie toutes les 10 secondes
                // On considère qu'un appareil est "mort" s'il n'a pas fait de signe depuis 30s
                discoveryManager.cleanupInactiveDevices(timeoutMillis = 30000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Arrêt propre des ressources réseau
        networkManager.stopServer()
        discoveryManager.stopDiscovery()
    }

    fun trySync(data: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            // On utilise la valeur actuelle du StateFlow pour itérer sur les IPs trouvées
            ips.value.forEach { ip ->
                networkManager.sendPacket(ip, messagePort, data)
            }
        }
    }

    // --- Fonctions Repository ---
    fun saveEntry(entry: Entry, secret: EntrySecret) = viewModelScope.launch { repository.saveEntry(entry, secret) }
    fun deleteEntry(entry: Entry) = viewModelScope.launch { repository.deleteEntry(entry) }
    fun saveGroup(group: Group, secret: GroupSecret) = viewModelScope.launch { repository.saveGroup(group, secret) }
    fun deleteGroup(group: Group) = viewModelScope.launch { repository.deleteGroup(group) }
    fun onSearchQueryChange(newQuery: String) { _searchQuery.value = newQuery }
    fun onGroupQueryChange(newGroup: Long) { _groupQuery.value = newGroup }

    companion object {
        fun Factory(repository: VaultRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application).applicationContext
                VaultViewModel(repository, context)
            }
        }
    }
}