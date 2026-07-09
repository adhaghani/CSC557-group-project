package com.example.groupproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun usernameCount(username: String): Int

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun emailCount(email: String): Int
}
