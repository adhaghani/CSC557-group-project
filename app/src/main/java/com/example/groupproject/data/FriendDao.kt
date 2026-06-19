package com.example.groupproject.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FriendDao {

    @Insert
    suspend fun insert(friend: Friend)

    @Update
    suspend fun update(friend: Friend)

    @Delete
    suspend fun delete(friend: Friend)

    @Query("SELECT * FROM friends WHERE id = :id")
    suspend fun getById(id: Int): Friend?

    @Query("SELECT * FROM friends ORDER BY name ASC")
    fun getAll(): LiveData<List<Friend>>

    @Query("SELECT * FROM friends WHERE name LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): LiveData<List<Friend>>

    @Query("SELECT gender AS name, COUNT(*) AS count FROM friends GROUP BY gender")
    fun getGenderCounts(): LiveData<List<CountResult>>

    @Query("SELECT addressLine4 AS name, COUNT(*) AS count FROM friends GROUP BY addressLine4 ORDER BY count DESC")
    fun getStateCounts(): LiveData<List<CountResult>>
}

data class CountResult(
    val name: String,
    val count: Int
)
