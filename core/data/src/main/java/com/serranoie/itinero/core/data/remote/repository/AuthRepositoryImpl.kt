package com.serranoie.itinero.core.data.remote.repository

import android.content.Context
import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import com.serranoie.itinero.core.data.mappers.AuthResult
import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.data.remote.dto.LoginRequestDto
import com.serranoie.itinero.core.data.remote.dto.RegisterRequestDto
import com.serranoie.itinero.core.domain.model.RegisterRequest
import com.serranoie.itinero.core.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val prefs: AuthPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult {
        val response = client.post("http://127.0.0.1:8080/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(email, password))
        }.body<AuthResponse>()

        prefs.saveToken(response.token)
        return response.toDomain()
    }

    override suspend fun register(request: RegisterRequest): AuthResult {
        val requestDto = RegisterRequestDto(
            name = request.name,
            surname = request.surname,
            email = request.email,
            password = request.password,
            phone = request.phone
        )

        val response = client.post("http://127.0.0.1:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body<AuthResponse>()

        prefs.saveToken(response.token)
        return response.toDomain()
    }

    override suspend fun saveAuthToken(token: String) = prefs.saveToken(token)

    override suspend fun getAuthToken(): String? = prefs.getToken()

    override suspend fun logout() {
        TODO("Not yet implemented")
    }
}