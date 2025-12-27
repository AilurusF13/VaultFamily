package fr.ailurus.vaultfamily.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "groups")
data class Group (
    @PrimaryKey(autoGenerate = true)
    val groupId: Long = 0,
    val groupName: String = "self"
)

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