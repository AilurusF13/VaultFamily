package fr.ailurus.vaultfamily.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ailurus.vaultfamily.data.model.GroupSecret

@Dao
interface GroupSecretDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(groupSecret: GroupSecret): Long

    @Query("SELECT * FROM groupsecret WHERE groupId = :searchQuery")
    suspend fun findById(searchQuery: Long): GroupSecret?

    @Delete
    suspend fun delete(groupSecret: GroupSecret)
}
