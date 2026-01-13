package fr.ailurus.vaultfamily.data.model.uistate

import fr.ailurus.vaultfamily.data.model.Entry
import fr.ailurus.vaultfamily.data.model.Group

data class VaultUiState(
    val entries: List<Entry> = emptyList(),
    val groups: List<Group> = emptyList(),
    val searchQuery: String = "",
    val groupQuery: Long = 0L
)