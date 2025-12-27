package fr.ailurus.vaultfamily.data.repository

import android.content.Context
import androidx.room.withTransaction
import fr.ailurus.vaultfamily.data.model.*
import kotlinx.coroutines.flow.Flow

class VaultRepositoryImpl(
) : VaultRepository {

    private var _db: AppDatabase? = null
    fun initializeDb(context: Context, passphrase: ByteArray) {
        _db = AppDatabase.getInstance(context, passphrase)
    }
    val database: AppDatabase
        get() = _db ?: throw IllegalStateException("Database not accessible")

    // ENTRY EDITING
    override fun getAllEntries(): Flow<List<Entry>> {
        return database.entryDao().getAll()
    }

    override suspend fun saveEntry(entry: Entry, secret: EntrySecret) {
        database.withTransaction {
            val entryId = database.entryDao().insert(entry)
            val linkedSecret = secret.copy(entryId = entryId)
            database.entrySecretDao().insert(linkedSecret)
        }
    }

    override suspend fun deleteEntry(entry: Entry) {
        database.withTransaction {
            database.entryDao().delete(entry)
        }
    }

    // FETCH SECRET ENTRY
    override suspend fun getSecret(entry: Entry): EntrySecret? {
        return database.entrySecretDao().findById(entry.entryId)
    }

    // GROUP EDITING
    override fun getAllGroups(): Flow<List<Group>> {
        return database.groupDao().getAll()
    }

    override suspend fun saveGroup(
        group: Group,
        secret: GroupSecret
    ) {
        database.withTransaction {
            val groupId = database.groupDao().insert(group)
            val linkedSecret = secret.copy(groupId = groupId)
            database.groupSecretDao().insert(linkedSecret)
        }
    }

    override suspend fun deleteGroup(group: Group) {
        database.withTransaction {
            database.groupDao().delete(group)
        }
    }

    // FETCH SECRET GROUP
    override suspend fun getSecret(group: Group): GroupSecret? {
        return database.groupSecretDao().findById(group.groupId)
    }
}