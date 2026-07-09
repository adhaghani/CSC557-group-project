package com.example.groupproject.util

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply()

    var loggedInUserId: Int
        get() = prefs.getInt(KEY_USER_ID, -1)
        set(value) = prefs.edit().putInt(KEY_USER_ID, value).apply()

    var loggedInUsername: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "buddy_session"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
    }
}
