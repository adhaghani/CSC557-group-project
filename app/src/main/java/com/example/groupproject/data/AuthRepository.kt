package com.example.groupproject.data

import android.content.Context
import com.example.groupproject.util.PasswordUtils

class AuthRepository(context: Context) {

    private val dao: UserDao = FriendDatabase.getInstance(context).userDao()

    suspend fun getUserByUsername(username: String): User? = dao.getByUsername(username)

    suspend fun isUsernameTaken(username: String): Boolean = dao.usernameCount(username) > 0

    suspend fun isEmailTaken(email: String): Boolean = dao.emailCount(email) > 0

    suspend fun registerUser(username: String, email: String, password: String): Long {
        val salt = PasswordUtils.generateSalt()
        val hash = PasswordUtils.hashPassword(password, salt)
        val user = User(username = username, email = email, passwordHash = hash, salt = salt)
        return dao.insert(user)
    }
}
