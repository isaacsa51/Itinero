package com.serranoie.itinero.core.domain.repository

import com.serranoie.itinero.core.domain.model.UserProfile

interface AuthPreferencesRepository {
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveUserId(userId: Int)
    fun getUserId(): Int?
    fun setOnboardingCompleted()
    fun isOnboardingCompleted(): Boolean
    fun saveLoginStatus(isLoggedIn: Boolean, expirationTimeMillis: Long? = null)
    fun isUserLoggedIn(): Boolean
    fun clearLoginStatus()
    fun clearToken()

    // User profile information
    fun saveUserName(name: String)
    fun getUserName(): String?
    fun saveUserLastName(lastName: String)
    fun getUserLastName(): String?
    fun saveUserEmail(email: String)
    fun getUserEmail(): String?
    fun saveUserPhone(phone: String)
    fun getUserPhone(): String?
    fun clearUserInfo()

    // Convenience methods for user profile
    fun getUserProfile(): UserProfile?
    fun setUserProfile(profile: UserProfile)
}