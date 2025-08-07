package com.serranoie.app.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.app.feature.auth.AuthConstants
import com.serranoie.itinero.core.domain.exception.UnauthorizedException
import com.serranoie.itinero.core.domain.model.RegisterRequest
import com.serranoie.itinero.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val userName: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authUseCase: AuthUseCase,
    private val authPreferences: AuthPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = authUseCase.login(email, password)
                Log.d("ITINERO - AuthViewModel", "Login successful, saving token: ${result.token}")
                authUseCase.saveAuthToken(result.token)

                val expirationTime =
                    System.currentTimeMillis() + AuthConstants.LOGIN_EXPIRATION_TIME
                authPreferences.saveLoginStatus(true, expirationTime)

                // Verify token was saved
                val savedToken = authPreferences.getToken()
                Log.d("ITINERO - AuthViewModel", "Token verification after save: $savedToken")

                _uiState.value = AuthUiState.Success(result.name)
            } catch (e: UnauthorizedException) {
                Log.e("ITINERO - AuthViewModel", "Login unauthorized: ${e.message}")
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Authentication failed. Please check your credentials."
                )
            } catch (e: Exception) {
                Log.e("ITINERO - AuthViewModel", "Login error: ${e.message}")
                _uiState.value = AuthUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun register(name: String, surname: String, phone: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val registerRequest = RegisterRequest(name, surname, phone, email, password)
                val result = authUseCase.register(registerRequest)
                authUseCase.saveAuthToken(result.token)

                val expirationTime =
                    System.currentTimeMillis() + AuthConstants.LOGIN_EXPIRATION_TIME
                authPreferences.saveLoginStatus(true, expirationTime)
                
                _uiState.value = AuthUiState.Success(result.name)
            } catch (e: UnauthorizedException) {
                Log.e("ITINERO - AuthViewModel", "Registration unauthorized: ${e.message}")
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Registration failed. Please check your information."
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
