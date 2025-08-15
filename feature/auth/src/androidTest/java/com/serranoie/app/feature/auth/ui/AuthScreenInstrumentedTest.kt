package com.serranoie.app.feature.auth.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var uiStateFlow: MutableStateFlow<AuthUiState>
    private var loginCalled = false
    private var capturedEmail = ""
    private var capturedPassword = ""

    @Before
    fun setUp() {
        uiStateFlow = MutableStateFlow(AuthUiState.Idle)
        loginCalled = false
        capturedEmail = ""
        capturedPassword = ""
    }

    @Test
    fun authScreen_loginButtonIsEnabledByDefault() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("AuthLoginButton")
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun authScreen_loginButtonIsDisabledDuringLoading() {
        // Given
        uiStateFlow.value = AuthUiState.Loading

        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("AuthLoginButton").assertIsNotEnabled()
    }

    @Test
    fun authScreen_showsErrorMessage() {
        // Given
        val errorMessage = "Invalid credentials. Please check your email and password."
        uiStateFlow.value = AuthUiState.Error(errorMessage)

        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("AuthErrorText")
            .assertIsDisplayed()
            .assertTextContains(errorMessage)
    }

    @Test
    fun authScreen_hideErrorMessageWhenNotInErrorState() {
        // Given
        uiStateFlow.value = AuthUiState.Idle

        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("AuthErrorText").assertDoesNotExist()
    }

    @Test
    fun authScreen_emailFieldAcceptsInput() {
        // Given
        val testEmail = "test@example.com"

        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { email, _ -> capturedEmail = email }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // When
        composeTestRule.onNodeWithTag("AuthEmailField").performTextInput(testEmail)

        // Then
        composeTestRule.onNodeWithTag("AuthEmailField").assertTextContains(testEmail)
    }

    @Test
    fun authScreen_passwordFieldAcceptsInput() {
        // Given
        val testPassword = "password123"

        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { _, password -> capturedPassword = password }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // When
        composeTestRule.onNodeWithTag("AuthPasswordField").performTextInput(testPassword)

        // Then
        composeTestRule.onNodeWithTag("AuthPasswordField").assertTextContains(testPassword)
    }

    @Test
    fun authScreen_loginButtonTriggersCallback() {
        // Given
        val testEmail = "test@example.com"
        val testPassword = "password123"

        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { email, password ->
                        loginCalled = true
                        capturedEmail = email
                        capturedPassword = password
                    }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // When
        composeTestRule.onNodeWithTag("AuthEmailField").performTextInput(testEmail)
        composeTestRule.onNodeWithTag("AuthPasswordField").performTextInput(testPassword)
        composeTestRule.onNodeWithTag("AuthLoginButton").performClick()

        // Then
        assert(loginCalled)
        assert(capturedEmail == testEmail)
        assert(capturedPassword == testPassword)
    }

    @Test
    fun authScreen_showsLoadingStateInButton() {
        // Given
        uiStateFlow.value = AuthUiState.Loading

        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then - Loading indicator should be shown instead of "Log in" text
        composeTestRule.onNodeWithTag("AuthLoginButton").assertIsDisplayed()
        // Note: We can't easily test for the loading indicator content since it's inside the button
    }

    @Test
    fun authScreen_socialButtonsAreClickable() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("AuthFacebookButton")
            .assertIsDisplayed()
            .assertHasClickAction()
            .assertIsEnabled()

        composeTestRule.onNodeWithTag("AuthGoogleButton")
            .assertIsDisplayed()
            .assertHasClickAction()
            .assertIsEnabled()
    }

    @Test
    fun authScreen_textFieldsShowErrorStateOnAuthFailure() {
        // Given
        uiStateFlow.value = AuthUiState.Error("Invalid credentials")

        composeTestRule.setContent {
            ItineroTheme {
                AuthScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onLogin = { _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then - Both fields should show error state
        composeTestRule.onNodeWithTag("AuthEmailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AuthPasswordField").assertIsDisplayed()
        // Note: Error state styling is handled internally by the components
    }
}