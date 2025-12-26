package fr.ailurus.vaultfamily.data.repository

import fr.ailurus.vaultfamily.data.model.Entry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

class FakeVaultRepository : VaultRepository {

    private val currentIdCounter = AtomicLong(0)

    private val _entries = MutableStateFlow<List<Entry>>(emptyList())

    override fun getAllEntries(): Flow<List<Entry>> {
        return _entries.asStateFlow()
    }

    override fun saveEntry(entry: Entry) {
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

    override fun deleteEntry(entry: Entry) {
        _entries.update { currentList ->
            currentList.filterNot { it.id == entry.id }
        }
    }
}