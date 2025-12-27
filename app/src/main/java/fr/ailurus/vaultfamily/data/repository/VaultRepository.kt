package fr.ailurus.vaultfamily.data.repository

import fr.ailurus.vaultfamily.data.model.*
import kotlinx.coroutines.flow.Flow

interface VaultRepository {

    // ENTRY EDITING
    fun getAllEntries(): Flow<List<Entry>>

    suspend fun saveEntry(entry : Entry, secret: EntrySecret)

    suspend fun deleteEntry(entry : Entry)

    // SECRET ENTRY FETCH
    suspend fun getSecret(entry: Entry): EntrySecret?

    // GROUP EDITING
    fun getAllGroups(): Flow<List<Group>>

    suspend fun saveGroup(group: Group, secret: GroupSecret)

    suspend fun deleteGroup(group: Group)

    // SECRET GROUP FETCH
    suspend fun getSecret(group: Group): GroupSecret?

}