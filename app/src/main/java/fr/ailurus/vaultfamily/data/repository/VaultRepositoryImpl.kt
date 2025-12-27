package fr.ailurus.vaultfamily.data.repository

import android.content.Context
import androidx.room.withTransaction
import fr.ailurus.vaultfamily.data.model.*
import kotlinx.coroutines.flow.Flow

class VaultRepositoryImpl(
    private val context: Context
) : VaultRepository {

    private var db: AppDatabase? = null

    fun initializeDb(passphrase: ByteArray) {
        db = AppDatabase.getInstance(context, passphrase)
    }

    // ENTRY EDITING
    override fun getAllEntries(): Flow<List<Entry>> {
        val database = db?: throw IllegalStateException("Database Locked")
        return database.EntryDao().getAll()
    }

    override suspend fun saveEntry(entry: Entry, secret: EntrySecret) {
        val database = db?: throw IllegalStateException("Database Locked")
        database.withTransaction {
            val entryId = database.EntryDao().insert(entry)
            val linkedSecret = secret.copy(entryId = entryId)
            database.EntrySecretDao().insert(linkedSecret)
        }
    }

    override suspend fun deleteEntry(entry: Entry) {
        val database = db?: throw IllegalStateException("Database Locked")
        database.withTransaction {
            database.EntryDao().delete(entry)
        }
    }

    // FETCH SECRET ENTRY
    override suspend fun getSecret(entry: Entry): EntrySecret? {
        val database = db?: throw IllegalStateException("Database Locked")
        return database.EntrySecretDao().findById(entry.entryId)
    }

    // GROUP EDITING
    override fun getAllGroups(): Flow<List<Group>> {
        val database = db?: throw IllegalStateException("Database Locked")
        return database.GroupDao().getAll()
    }

    override suspend fun saveGroup(
        group: Group,
        secret: GroupSecret
    ) {
        val database = db?: throw IllegalStateException("Database Locked")
        database.withTransaction {
            val groupId = database.GroupDao().insert(group)
            val linkedSecret = secret.copy(groupId = groupId)
            database.GroupSecretDao().insert(linkedSecret)
        }
    }

    override suspend fun deleteGroup(group: Group) {
        val database = db?: throw IllegalStateException("Database Locked")
        database.withTransaction {
            database.GroupDao().delete(group)
        }
    }

    // FETCH SECRET GROUP
    override suspend fun getSecret(group: Group): GroupSecret? {
        val database = db?: throw IllegalStateException("Database Locked")
        return database.GroupSecretDao().findById(group.groupId)
    }
}