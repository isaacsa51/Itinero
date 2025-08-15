package com.serranoie.app.feature.auth.ui

import app.cash.turbine.test
import com.serranoie.itinero.core.domain.exception.NetworkException
import com.serranoie.itinero.core.domain.exception.UnauthorizedException
import com.serranoie.itinero.core.domain.model.RegisterRequest
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.result.AuthResult
import com.serranoie.itinero.core.domain.usecase.AuthUseCase
import com.serranoie.itinero.core.domain.usecase.LoginUseCase
import com.serranoie.itinero.core.domain.usecase.RegisterUseCase
import com.serranoie.itinero.core.domain.usecase.SaveAuthTokenUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var authUseCase: AuthUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var saveAuthTokenUseCase: SaveAuthTokenUseCase
    private lateinit var authPreferences: AuthPreferencesRepository
    private lateinit var viewModel: AuthViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        loginUseCase = mockk()
        registerUseCase = mockk()
        saveAuthTokenUseCase = mockk(relaxed = true)
        authPreferences = mockk(relaxed = true)

        authUseCase = AuthUseCase(
            login = loginUseCase,
            register = registerUseCase,
            getAuthToken = mockk(),
            saveAuthToken = saveAuthTokenUseCase,
            logout = mockk(),
            deleteAccountUseCase = mockk()
        )

        viewModel = AuthViewModel(authUseCase, authPreferences)
    }

    @Test
    fun `login with valid credentials should emit Success state`() = runTest(mainDispatcherRule.testDispatcher) {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val authResult = AuthResult(
            token = "test_token",
            userId = 123,
            name = "John",
            lastName = "Doe"
        )

        coEvery { loginUseCase(email, password) } returns authResult

        // When
        viewModel.login(email, password)
        advanceUntilIdle()

        // Then - Verify final state
        val finalState = viewModel.uiState.value
        assertTrue("Expected Success state, but got $finalState", finalState is AuthUiState.Success)
        assertEquals("John", (finalState as AuthUiState.Success).userName)

        // Verify interactions
        coVerify(exactly = 1) { loginUseCase(email, password) }
        coVerify(exactly = 1) { saveAuthTokenUseCase("test_token") }
        verify(exactly = 1) { authPreferences.saveLoginStatus(true, any()) }
    }

    @Test
    fun `login with invalid credentials should emit Error state`() = runTest(mainDispatcherRule.testDispatcher) {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        val exception = UnauthorizedException("Invalid credentials")

        coEvery { loginUseCase(email, password) } throws exception

        // When
        viewModel.login(email, password)
        advanceUntilIdle()

        // Then - Verify final state
        val finalState = viewModel.uiState.value
        assertTrue("Expected Error state, but got $finalState", finalState is AuthUiState.Error)
        assertEquals("Invalid credentials", (finalState as AuthUiState.Error).message)

        coVerify(exactly = 1) { loginUseCase(email, password) }
        coVerify(exactly = 0) { saveAuthTokenUseCase(any()) }
    }

    @Test
    fun `login with network error should emit Error state with generic message`() = runTest(mainDispatcherRule.testDispatcher) {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val exception = NetworkException("Network error")

        coEvery { loginUseCase(email, password) } throws exception

        // When
        viewModel.login(email, password)
        advanceUntilIdle()

        // Then - Verify final state
        val finalState = viewModel.uiState.value
        assertTrue("Expected Error state, but got $finalState", finalState is AuthUiState.Error)
        assertEquals("Network error", (finalState as AuthUiState.Error).message)
    }

    @Test
    fun `login should emit Loading state during execution`() = runTest(mainDispatcherRule.testDispatcher) {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val authResult = AuthResult(
            token = "token",
            userId = 1,
            name = "John",
            lastName = "Doe"
        )

        coEvery { loginUseCase(email, password) } coAnswers {
            kotlinx.coroutines.delay(10)
            authResult
        }

        // When & Then - Test state transitions
        viewModel.uiState.test {
            // Initial state should be Idle
            assertEquals(AuthUiState.Idle, awaitItem())

            // Trigger login
            viewModel.login(email, password)

            // Should emit Loading state
            assertEquals(AuthUiState.Loading, awaitItem())

            // Advance to allow the delayed success to complete
            advanceUntilIdle()

            // Should emit Success state
            val successState = awaitItem()
            assertTrue(successState is AuthUiState.Success)
        }
    }

    @Test
    fun `register with valid data should emit Success state`() = runTest(mainDispatcherRule.testDispatcher) {
        // Given
        val name = "John"
        val surname = "Doe"
        val phone = "1234567890"
        val email = "test@example.com"
        val password = "password123"
        val authResult = AuthResult(
            token = "token",
            userId = 1,
            name = "John",
            lastName = "Doe"
        )

        coEvery { registerUseCase(any()) } returns authResult

        // When
        viewModel.register(name, surname, phone, email, password)
        advanceUntilIdle()

        // Then - Verify final state
        val finalState = viewModel.uiState.value
        assertTrue("Expected Success state, but got $finalState", finalState is AuthUiState.Success)
        assertEquals("John", (finalState as AuthUiState.Success).userName)

        // Verify interactions
        coVerify(exactly = 1) {
            registerUseCase(
                RegisterRequest(name, surname, phone, email, password)
            )
        }
        coVerify(exactly = 1) { saveAuthTokenUseCase("token") }
        verify(exactly = 1) { authPreferences.saveLoginStatus(true, any()) }
    }

    @Test
    fun `register with invalid data should emit Error state`() = runTest(mainDispatcherRule.testDispatcher) {
        // Given
        val name = "John"
        val surname = "Doe"
        val phone = "1234567890"
        val email = "invalid_email"
        val password = "weak"
        val exception = UnauthorizedException("Registration failed")

        coEvery { registerUseCase(any()) } throws exception

        // When
        viewModel.register(name, surname, phone, email, password)
        advanceUntilIdle()

        // Then - Verify final state
        val finalState = viewModel.uiState.value
        assertTrue("Expected Error state, but got $finalState", finalState is AuthUiState.Error)
        assertEquals("Registration failed", (finalState as AuthUiState.Error).message)
    }

    @Test
    fun `register should emit Loading state during execution`() = runTest(mainDispatcherRule.testDispatcher) {
        // Given
        val name = "John"
        val surname = "Doe"
        val phone = "1234567890"
        val email = "test@example.com"
        val password = "password123"
        val authResult = AuthResult(
            token = "token",
            userId = 1,
            name = "John",
            lastName = "Doe"
        )

        coEvery { registerUseCase(any()) } coAnswers {
            kotlinx.coroutines.delay(10)
            authResult
        }

        // When & Then - Test state transitions
        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())

            viewModel.register(name, surname, phone, email, password)

            assertEquals(AuthUiState.Loading, awaitItem())

            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is AuthUiState.Success)
        }
    }

    @Test
    fun `initial state should be Idle`() = runTest(mainDispatcherRule.testDispatcher) {
        // When & Then
        val initialState = viewModel.uiState.value
        assertTrue("Expected Idle state, but got $initialState", initialState is AuthUiState.Idle)
        assertEquals(AuthUiState.Idle, initialState)
    }
}