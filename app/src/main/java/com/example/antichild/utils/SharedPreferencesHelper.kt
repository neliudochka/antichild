package com.example.antichild.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.antichild.models.Child
import com.example.antichild.models.Parent
import com.google.firebase.auth.FirebaseAuth

object SharedPreferencesHelper {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_CHILD_UID = "uid"
    private const val KEY_CHILD_USERNAME = "username"
    private const val KEY_CHILD_EMAIL = "email"
    private const val KEY_CHILD_ROLE = "role"
    private const val KEY_PARENT_UID = "parentUid"
    private const val KEY_PARENT_NAME = "parentName"
    private const val KEY_PARENT_EMAIL = "parentEmail"
    private const val KEY_PARENT_ROLE = "parentRole"
    private const val KEY_PARENT_ACCESS_PASSWORD = "accessPassword"
    private const val KEY_BUTTON_STATE = "isActivated"
    private const val CHILD_UID_DIALOG = "childUidDialog"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveParentUserData(parent: Parent) {
        with(sharedPreferences.edit()) {
            putString(KEY_PARENT_UID, parent.uid)
            putString(KEY_PARENT_NAME, parent.username)
            putString(KEY_PARENT_EMAIL, parent.email)
            putString(KEY_PARENT_ACCESS_PASSWORD, parent.accessPassword)
            putString(KEY_PARENT_ROLE, parent.role)
            apply()
        }
    }

    fun saveChildUserData(child: Child, parent: Parent) {
        saveParentUserData(parent)
        with(sharedPreferences.edit()) {
            putString(KEY_CHILD_UID, child.uid)
            putString(KEY_CHILD_USERNAME, child.username)
            putString(KEY_CHILD_EMAIL, child.email)
            putString(KEY_CHILD_ROLE, child.role)
            apply()
        }
    }

    fun getUserData(): UserData {
        return if (FirebaseAuth.getInstance().uid == sharedPreferences.getString(KEY_CHILD_UID, null)) {
            ChildData(
                sharedPreferences.getString(KEY_CHILD_UID, null),
                sharedPreferences.getString(KEY_CHILD_USERNAME, null),
                sharedPreferences.getString(KEY_CHILD_EMAIL, null),
                sharedPreferences.getString(KEY_PARENT_UID, null),
                sharedPreferences.getString(KEY_PARENT_NAME, null),
                sharedPreferences.getString(KEY_PARENT_EMAIL, null),
                sharedPreferences.getString(KEY_CHILD_ROLE, null),
                sharedPreferences.getString(KEY_PARENT_ACCESS_PASSWORD, null)
            )
        } else {
            ParentData(
                sharedPreferences.getString(KEY_PARENT_UID, null),
                sharedPreferences.getString(KEY_PARENT_NAME, null),
                sharedPreferences.getString(KEY_PARENT_EMAIL, null),
                sharedPreferences.getString(KEY_PARENT_ROLE, null),
                sharedPreferences.getString(KEY_PARENT_ACCESS_PASSWORD, null)
            )
        }
    }

    fun getParentData(): ParentData {
        return ParentData(
            sharedPreferences.getString(KEY_PARENT_UID, null),
            sharedPreferences.getString(KEY_PARENT_NAME, null),
            sharedPreferences.getString(KEY_PARENT_EMAIL, null),
            sharedPreferences.getString(KEY_PARENT_ROLE, null),
            sharedPreferences.getString(KEY_PARENT_ACCESS_PASSWORD, null)
        )
    }

    fun getChildData(): ChildData {
        return ChildData(
            sharedPreferences.getString(KEY_CHILD_UID, null),
            sharedPreferences.getString(KEY_CHILD_USERNAME, null),
            sharedPreferences.getString(KEY_CHILD_EMAIL, null),
            sharedPreferences.getString(KEY_PARENT_UID, null),
            sharedPreferences.getString(KEY_PARENT_NAME, null),
            sharedPreferences.getString(KEY_PARENT_EMAIL, null),
            sharedPreferences.getString(KEY_CHILD_ROLE, null),
            sharedPreferences.getString(KEY_PARENT_ACCESS_PASSWORD, null)
        )
    }

    fun clearUserData() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    fun saveParentButtonState(isButtonOn: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_BUTTON_STATE, isButtonOn)
            apply()
        }
    }

    fun getParentButtonState(): Boolean {
        return sharedPreferences.getBoolean(KEY_BUTTON_STATE, false)
    }

    fun saveCurrentChild(childUid: String) {
        with(sharedPreferences.edit()) {
            putString(CHILD_UID_DIALOG, childUid)
            apply()
        }
    }

    fun getCurrentChild(): String? {
        return sharedPreferences.getString(CHILD_UID_DIALOG,null)
    }
}

