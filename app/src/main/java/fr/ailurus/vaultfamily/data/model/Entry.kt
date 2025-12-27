package fr.ailurus.vaultfamily.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
