package com.serranoie.itinero.core.data.remote.repository

import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.remote.resources.ItineroApi
import com.serranoie.itinero.core.domain.model.RegisterRequest
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.result.AuthResult

class AuthRepositoryImpl(
    private val api: ItineroApi,
    private val authPreferencesRepository: AuthPreferencesRepository
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult {
        val response = api.loginUser(email, password)
        val authResult = response.toDomain()
        authPreferencesRepository.saveToken(authResult.token)
        authPreferencesRepository.saveUserId(authResult.userId)
        authPreferencesRepository.saveUserName(authResult.name)
        authPreferencesRepository.saveUserLastName(authResult.lastName)
        authPreferencesRepository.saveUserEmail(email)
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
        authPreferencesRepository.saveToken(authResult.token)
        authPreferencesRepository.saveUserId(authResult.userId)
        authPreferencesRepository.saveUserName(authResult.name)
        authPreferencesRepository.saveUserLastName(authResult.lastName)
        authPreferencesRepository.saveUserEmail(request.email)
        authPreferencesRepository.saveUserPhone(request.phone)
        return authResult
    }

    override suspend fun saveAuthToken(token: String) = authPreferencesRepository.saveToken(token)

    override suspend fun getAuthToken(): String? = authPreferencesRepository.getToken()

    override suspend fun logout() {
        authPreferencesRepository.clearToken()
        authPreferencesRepository.clearLoginStatus()
        authPreferencesRepository.saveLoginStatus(false)
        authPreferencesRepository.clearUserInfo()
    }

    override suspend fun deleteAccount(password: String) {
        try {
            api.deleteAccount(password)
            authPreferencesRepository.clearToken()
            authPreferencesRepository.clearLoginStatus()
            authPreferencesRepository.saveLoginStatus(false)
            authPreferencesRepository.clearUserInfo()
        } catch (e: Exception) {
            throw e
        }
    }
}
