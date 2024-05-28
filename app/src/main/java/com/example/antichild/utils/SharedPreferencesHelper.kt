package com.example.antichild.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_UID = "uid"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"
    private const val KEY_ADVANCE = "advance"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserData(uid: String, username: String, email: String, role: String, advance: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_UID, uid)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            putString(KEY_ROLE, role)
            putString(KEY_ADVANCE, advance)
            apply()
        }
    }

    fun getUserData(): UserData {
        return UserData(
            sharedPreferences.getString(KEY_UID, null),
            sharedPreferences.getString(KEY_USERNAME, null),
            sharedPreferences.getString(KEY_EMAIL, null),
            sharedPreferences.getString(KEY_ROLE, null),
            sharedPreferences.getString(KEY_ADVANCE, null)
        )
    }

    fun clearUserData() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}

