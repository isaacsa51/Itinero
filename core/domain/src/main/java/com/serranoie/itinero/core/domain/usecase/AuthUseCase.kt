package com.serranoie.itinero.core.domain.usecase

import com.serranoie.itinero.core.domain.model.AuthResult
import com.serranoie.itinero.core.domain.model.RegisterRequest
import com.serranoie.itinero.core.domain.repository.AuthRepository

data class AuthUseCase(
    val login: LoginUseCase,
    val register: RegisterUseCase,
    val getAuthToken: GetAuthTokenUseCase,
    val saveAuthToken: SaveAuthTokenUseCase,
    val logout: LogoutUseCase
)

class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) : AuthResult = repo.login(email, password)
}

class RegisterUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(registerRequest: RegisterRequest) : AuthResult = repo.register(registerRequest)
}

class GetAuthTokenUseCase(private val repo: AuthRepository) {
    operator fun invoke() : String? = repo.getAuthToken()
}

class SaveAuthTokenUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(token: String) = repo.saveAuthToken(token)
}

class LogoutUseCase(private val repo: AuthRepository) {
//    operator fun invoke() = repo.logout()
}