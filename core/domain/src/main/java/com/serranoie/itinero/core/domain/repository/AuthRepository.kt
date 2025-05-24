package com.serranoie.itinero.core.domain.repository

import com.serranoie.itinero.core.domain.model.AuthResult
import com.serranoie.itinero.core.domain.model.RegisterRequest

interface AuthRepository {

    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(request: RegisterRequest): AuthResult
    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    suspend fun logout()
}