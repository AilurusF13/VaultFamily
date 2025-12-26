package fr.ailurus.vaultfamily.data.repository

import fr.ailurus.vaultfamily.data.model.Entry
import kotlinx.coroutines.flow.Flow

interface VaultRepository {

    fun getAllEntries(): Flow<List<Entry>>

    fun saveEntry(entry : Entry)

    fun deleteEntry(entry : Entry)
}