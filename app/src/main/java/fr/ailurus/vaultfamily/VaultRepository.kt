package fr.ailurus.vaultfamily

import kotlinx.coroutines.flow.Flow

interface VaultRepository {

    fun getAllEntries(): Flow<List<ContentLine>>

    fun saveEntry(entry : ContentLine)

    fun deleteEntry(entry : ContentLine)
}