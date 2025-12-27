package fr.ailurus.vaultfamily.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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

        if (groupId != other.groupId) return false
        if (!groupKey.contentEquals(other.groupKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupId.hashCode()
        result = 31 * result + groupKey.contentHashCode()
        return result
    }
}
