package fr.ailurus.vaultfamily.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ailurus.vaultfamily.data.model.Entry
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry): Long

    @Query("SELECT * FROM entries ORDER BY entrySite ASC")
    fun getAll(): Flow<List<Entry>>

//    @Query("SELECT * FROM entries WHERE entryId = :searchQuery")
//    suspend fun findById(searchQuery: Long): Entry?

    @Delete
    suspend fun delete(entry: Entry)
}
