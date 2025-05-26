package com.serranoie.itinero.core.data.remote

import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.data.remote.dto.LoginRequestDto
import com.serranoie.itinero.core.data.remote.dto.RegisterRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ItineroApiImpl(
    private val client: HttpClient
) : ItineroApi {

    private companion object {
        const val BASE_URL = "http://10.0.2.2:8080"
    }

    override suspend fun loginUser(email: String, password: String): AuthResponse {
        return client.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(email, password))
        }.body()
    }

    override suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        surname: String,
        phone: String
    ): AuthResponse {
        return client.post("$BASE_URL/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequestDto(name, surname, email, password, phone))
        }.body()
    }

    override suspend fun logoutUser() {
        // TODO: Implement logout API call if needed
    }

    override suspend fun forgotPasswordUser(email: String) {
        // TODO: Implement forgot password API call
    }
}