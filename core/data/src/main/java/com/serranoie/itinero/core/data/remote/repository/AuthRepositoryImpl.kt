package com.serranoie.itinero.core.data.remote.repository

import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.remote.resources.ItineroApi
import com.serranoie.itinero.core.domain.result.AuthResult
import com.serranoie.itinero.core.domain.model.RegisterRequest
import com.serranoie.itinero.core.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: ItineroApi,
    private val prefs: AuthPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult {
        val response = api.loginUser(email, password)
        val authResult = response.toDomain()
        prefs.saveToken(authResult.token)
        prefs.saveUserId(authResult.userId)
        return authResult
    }

    override suspend fun register(request: RegisterRequest): AuthResult {
        val response = api.registerUser(
            email = request.email,
            password = request.password,
            name = request.name,
            surname = request.surname,
            phone = request.phone
        )
        val authResult = response.toDomain()
        prefs.saveToken(authResult.token)
        prefs.saveUserId(authResult.userId)
        return authResult
    }

    override suspend fun saveAuthToken(token: String) = prefs.saveToken(token)

    override suspend fun getAuthToken(): String? = prefs.getToken()

    override suspend fun logout() {
        prefs.clearToken()
    }
}
