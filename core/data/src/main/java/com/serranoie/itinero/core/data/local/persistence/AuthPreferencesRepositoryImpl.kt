package com.serranoie.itinero.core.data.local.persistence

import android.content.Context
import androidx.core.content.edit
import com.serranoie.itinero.core.domain.model.UserProfile
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import android.util.Log

// TODO: Change/encrypt all information to not be able to see the locally saved info by using EncryptedPrefs
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
        }
    }

    override fun clearToken() {
        prefs.edit { remove("token") }
    }

    override fun saveUserName(name: String) {
        prefs.edit { putString("user_name", name) }
    }

    override fun getUserName(): String? = prefs.getString("user_name", null)

    override fun saveUserLastName(lastName: String) {
        prefs.edit { putString("user_last_name", lastName) }
    }

    override fun getUserLastName(): String? = prefs.getString("user_last_name", null)

    override fun saveUserEmail(email: String) {
        prefs.edit { putString("user_email", email) }
    }

    override fun getUserEmail(): String? = prefs.getString("user_email", null)

    override fun saveUserPhone(phone: String) {
        prefs.edit { putString("user_phone", phone) }
    }

    override fun getUserPhone(): String? = prefs.getString("user_phone", null)

    override fun clearUserInfo() {
        prefs.edit {
            remove("user_name")
            remove("user_last_name")
            remove("user_email")
            remove("user_phone")
        }
    }

    override fun setUserProfile(profile: UserProfile) {
        prefs.edit {
            profile.id?.let { putInt("user_id", it) }
            putString("user_name", profile.name)
            putString("user_last_name", profile.lastName)
            putString("user_email", profile.email)
            profile.phone?.let { putString("user_phone", it) }
        }
    }

    override fun getUserProfile(): UserProfile? {
        val userId = if (prefs.contains("user_id")) prefs.getInt("user_id", -1) else null
        val name = prefs.getString("user_name", null)
        val lastName = prefs.getString("user_last_name", null)
        val email = prefs.getString("user_email", null)
        val phone = prefs.getString("user_phone", null)

        return if (name != null && lastName != null && email != null) {
            UserProfile(
                id = userId,
                name = name,
                lastName = lastName,
                email = email,
                phone = phone
            )
        } else {
            Log.d(
                "AuthPrefs",
                "Missing user profile data - name: $name, lastName: $lastName, email: $email"
            )
            null
        }
    }
}