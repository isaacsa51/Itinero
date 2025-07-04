package com.serranoie.itinero.core.data.local.persistence

import android.content.Context
import androidx.core.content.edit
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository

class AuthPreferencesRepositoryImpl(context: Context) : AuthPreferencesRepository {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        prefs.edit { putString("token", token) }
    }

    override fun getToken(): String? = prefs.getString("token", null)

    override fun saveUserId(userId: Int) {
        prefs.edit { putInt("user_id", userId) }
    }

    override fun getUserId(): Int? {
        return if (prefs.contains("user_id")) {
            prefs.getInt("user_id", -1).takeIf { it != -1 }
        } else {
            null
        }
    }

    override fun setOnboardingCompleted() {
        prefs.edit { putBoolean("onboarding_completed", true) }
    }

    override fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_completed", false)

    override fun saveLoginStatus(isLoggedIn: Boolean, expirationTimeMillis: Long?) {
        prefs.edit {
            putBoolean("is_logged_in", isLoggedIn)
            if (expirationTimeMillis != null) {
                putLong("login_expiration", expirationTimeMillis)
            }
        }
    }

    override fun isUserLoggedIn(): Boolean {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        if (!isLoggedIn) return false

        val expirationTime = prefs.getLong("login_expiration", 0L)
        if (expirationTime > 0 && System.currentTimeMillis() > expirationTime) {
            clearLoginStatus()
            return false
        }

        return true
    }

    override fun clearLoginStatus() {
        prefs.edit {
            remove("is_logged_in")
            remove("login_expiration")
            remove("token")
        }
    }

    override fun clearToken() {
        prefs.edit { clear() }
    }
}