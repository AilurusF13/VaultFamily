package fr.ailurus.vaultfamily.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ailurus.vaultfamily.data.model.EntrySecret

@Dao
interface EntrySecretDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entrySecret: EntrySecret): Long

    @Query("SELECT * FROM entrysecret WHERE entryId = :searchQuery")
    suspend fun findById(searchQuery: Long): EntrySecret?

    @Delete
    suspend fun delete(entrySecret: EntrySecret)
}
