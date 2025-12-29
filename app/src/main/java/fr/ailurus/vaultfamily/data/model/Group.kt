package fr.ailurus.vaultfamily.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group (
    @PrimaryKey(autoGenerate = true)
    val groupId: Long = 0,
    val groupName: String
)
