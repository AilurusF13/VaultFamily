package fr.ailurus.vaultfamily.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ailurus.vaultfamily.data.model.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: Group): Long

    @Query("SELECT * FROM groups ORDER BY groupId ASC")
    fun getAll(): Flow<List<Group>>

    @Query("SELECT * FROM groups WHERE groupId = :searchQuery")
    suspend fun findById(searchQuery: Long): Group?

    @Delete
    suspend fun delete(group: Group)
}
