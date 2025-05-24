package com.serranoie.itinero.core.data.remote

interface ItineroApi {
    suspend fun loginUser(email: String, password: String)
    suspend fun registerUser(email: String, password: String)
    suspend fun logoutUser()
    suspend fun forgotPasswordUser(email: String)
}