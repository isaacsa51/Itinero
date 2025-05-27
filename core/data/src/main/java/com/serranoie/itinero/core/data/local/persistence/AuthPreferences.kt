package com.serranoie.itinero.core.data.local.persistence

import android.content.Context
import androidx.core.content.edit

class AuthPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit { putString("token", token) }
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun setOnboardingCompleted() {
        prefs.edit { putBoolean("onboarding_completed", true) }
    }

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_completed", false)

    fun saveLoginStatus(isLoggedIn: Boolean, expirationTimeMillis: Long? = null) {
        prefs.edit {
            putBoolean("is_logged_in", isLoggedIn)
            if (expirationTimeMillis != null) {
                putLong("login_expiration", expirationTimeMillis)
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        if (!isLoggedIn) return false

        val expirationTime = prefs.getLong("login_expiration", 0L)
        if (expirationTime > 0 && System.currentTimeMillis() > expirationTime) {
            clearLoginStatus()
            return false
        }

        return true
    }

    fun clearLoginStatus() {
        prefs.edit {
            remove("is_logged_in")
            remove("login_expiration")
            remove("token")
        }
    }

    fun clear() {
        prefs.edit { clear() }
    }
}
