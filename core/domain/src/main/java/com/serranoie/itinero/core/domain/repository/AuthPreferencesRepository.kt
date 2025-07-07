package com.serranoie.itinero.core.domain.repository

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
}