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


@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"]
        )
    ]
)
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val entryId : Long = 0,
    val entrySite : String = "",
    val entryUser : String = "",
    val groupId : Long = 0
)

@Dao
interface EntryDao {
    // INSERTION : OnInsert_ConflictStrategy.REPLACE gère les doublons d'ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry): Long

    @Query("SELECT * FROM entries ORDER BY entrySite ASC")
    fun getAll(): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE entryId = :searchQuery")
    suspend fun findById(searchQuery: Long): Entry?

    @Delete
    suspend fun delete(entry: Entry)
}