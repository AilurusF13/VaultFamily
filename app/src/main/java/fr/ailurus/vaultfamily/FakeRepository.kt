package fr.ailurus.vaultfamily

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

class FakeVaultRepository : VaultRepository {

    private val currentIdCounter = AtomicLong(0)

    private val _entries = MutableStateFlow<List<ContentLine>>(emptyList())

    override fun getAllEntries(): Flow<List<ContentLine>> {
        return _entries.asStateFlow()
    }

    override fun saveEntry(entry: ContentLine) {
        _entries.update { currentList ->
            if (entry.id == 0L){
                val newId = currentIdCounter.incrementAndGet()
                val newEntry = entry.copy(id = newId)
                listOf(newEntry) + currentList
            } else {
                currentList.map {
                    if (it.id == entry.id) entry else it
                }
            }
        }
    }

    override fun deleteEntry(entry: ContentLine) {
        _entries.update { currentList ->
            currentList.filterNot { it.id == entry.id }
        }
    }
}