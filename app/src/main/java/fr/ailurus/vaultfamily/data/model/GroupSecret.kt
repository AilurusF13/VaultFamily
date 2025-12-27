package fr.ailurus.vaultfamily.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.ForeignKey
import androidx.room.OnConflictStrategy


@Entity(
    tableName = "groupsecret",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GroupSecret (
    @PrimaryKey
    val groupId: Long,
    val groupKey: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroupSecret

        if (!groupKey.contentEquals(other.groupKey)) return false

        return true
    }

    override fun hashCode(): Int {
        return groupKey.contentHashCode()
    }
}

@Dao
interface GroupSecretDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(groupSecret: GroupSecret): Long

    @Query("SELECT * FROM groupsecret WHERE groupId = :searchQuery")
    suspend fun findById(searchQuery: Long): GroupSecret?

    @Delete
    suspend fun delete(groupSecret: GroupSecret)
}