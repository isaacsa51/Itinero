package com.serranoie.itinero.core.data.remote.repository

import com.serranoie.itinero.core.data.mappers.AuthResult
import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val prefs: AuthPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult {
        val response: AuthResponse = client.post("http://your.api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }
        prefs.saveToken(response.token)
        return response.toDomain()
    }

    override suspend fun register(request: RegisterRequest): AuthResult {
        val response: AuthResponse = client.post("http://your.api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        prefs.saveToken(response.token)
        return response.toDomain()
    }

    override suspend fun saveAuthToken(token: String) = prefs.saveToken(token)
    override fun getAuthToken(): String? = prefs.getToken()
}