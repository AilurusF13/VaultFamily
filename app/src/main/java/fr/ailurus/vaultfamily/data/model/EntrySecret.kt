package fr.ailurus.vaultfamily.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "entrysecret",
    foreignKeys = [
        ForeignKey(
            entity = Entry::class,
            parentColumns = ["entryId"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EntrySecret (
    @PrimaryKey
    val entryId: Long,
    val encryptedPassword: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntrySecret

        if (entryId != other.entryId) return false
        if (!encryptedPassword.contentEquals(other.encryptedPassword)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = entryId.hashCode()
        result = 31 * result + encryptedPassword.contentHashCode()
        return result
    }
}
