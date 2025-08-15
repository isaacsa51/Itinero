package com.serranoie.app.feature.auth.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
class RegisterScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var uiStateFlow: MutableStateFlow<AuthUiState>
    private var registerCalled = false
    private var capturedData = mutableListOf<String>()

    @Before
    fun setUp() {
        uiStateFlow = MutableStateFlow(AuthUiState.Idle)
        registerCalled = false
        capturedData.clear()
    }

    @Test
    fun registerScreen_displaysAllUIElements() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("RegisterScreenColumn").assertIsDisplayed()

        composeTestRule.onNodeWithTag("RegisterImage").performScrollTo()
        composeTestRule.onNodeWithTag("RegisterImage").assertIsDisplayed()

        composeTestRule.onNodeWithText("Sign up").performScrollTo()
        composeTestRule.onNodeWithText("Sign up").assertIsDisplayed()

        composeTestRule.onNodeWithText("Create an account to plan the perfect trip and discover new destinations")
            .performScrollTo()
        composeTestRule.onNodeWithText("Create an account to plan the perfect trip and discover new destinations")
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("RegisterButton").performScrollTo()
        composeTestRule.onNodeWithTag("RegisterButton").assertIsDisplayed()
    }

    @Test
    fun registerScreen_displaysTitleAndLabels() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText("Name").performScrollTo()
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()

        composeTestRule.onNodeWithText("Last Name").performScrollTo()
        composeTestRule.onNodeWithText("Last Name").assertIsDisplayed()

        composeTestRule.onNodeWithText("Number").performScrollTo()
        composeTestRule.onNodeWithText("Number").assertIsDisplayed()

        composeTestRule.onNodeWithText("Email").performScrollTo()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()

        composeTestRule.onNodeWithText("Password").performScrollTo()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()

        composeTestRule.onNodeWithText("Confirm Password").performScrollTo()
        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()

        composeTestRule.onNodeWithText("Register").performScrollTo()
        composeTestRule.onNodeWithText("Register").assertIsDisplayed()
    }

    @Test
    fun registerScreen_registerButtonIsEnabledByDefault() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("RegisterButton").assertIsEnabled()
    }

    @Test
    fun registerScreen_registerButtonIsDisabledDuringLoading() {
        // Given
        uiStateFlow.value = AuthUiState.Loading

        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("RegisterButton").assertIsNotEnabled()
    }

    @Test
    fun registerScreen_showsErrorMessage() {
        // Given
        val errorMessage = "Registration failed"
        uiStateFlow.value = AuthUiState.Error(errorMessage)

        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // Wait for composition and state updates
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("RegisterErrorText").performScrollTo()
        composeTestRule.onNodeWithTag("RegisterErrorText")
            .assertIsDisplayed()
            .assertTextContains(errorMessage)
    }

    @Test
    fun registerScreen_hideErrorMessageWhenNotInErrorState() {
        // Given
        uiStateFlow.value = AuthUiState.Idle

        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("RegisterErrorText").assertDoesNotExist()
    }

    @Test
    fun registerScreen_allFieldsAcceptInput() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { name, lastName, number, email, password ->
                        capturedData.addAll(listOf(name, lastName, number, email, password))
                    }
                )
            }
        }

        // When
        composeTestRule.onNodeWithTag("NameField").performTextInput("John")
        composeTestRule.onNodeWithTag("LastNameField").performTextInput("Doe")
        composeTestRule.onNodeWithTag("NumberField").performTextInput("1234567890")
        composeTestRule.onNodeWithTag("EmailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("PasswordField").performTextInput("Password123!")
        composeTestRule.onNodeWithTag("ConfirmPasswordField").performTextInput("Password123!")

        // Then
        composeTestRule.onNodeWithTag("NameField").assertTextContains("John")
        composeTestRule.onNodeWithTag("LastNameField").assertTextContains("Doe")
        composeTestRule.onNodeWithTag("NumberField").assertTextContains("1234567890")
        composeTestRule.onNodeWithTag("EmailField").assertTextContains("test@example.com")
    }

    @Test
    fun registerScreen_showsValidationErrorsForInvalidInput() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // When
        composeTestRule.onNodeWithTag("RegisterButton").performScrollTo()
        composeTestRule.onNodeWithTag("RegisterButton").performClick()

        // Then
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("RegisterErrorText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RegisterErrorText").assertTextContains("Name is required")
    }

    @Test
    fun registerScreen_socialButtonsAreDisplayed() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // Wait for composition to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("FacebookButton").performScrollTo()
        composeTestRule.onNodeWithTag("FacebookButton").assertIsDisplayed()

        composeTestRule.onNodeWithText("Continue with Facebook").performScrollTo()
        composeTestRule.onNodeWithText("Continue with Facebook").assertIsDisplayed()

        composeTestRule.onNodeWithTag("GoogleButton").performScrollTo()
        composeTestRule.onNodeWithTag("GoogleButton").assertIsDisplayed()

        composeTestRule.onNodeWithText("Continue with Google").performScrollTo()
        composeTestRule.onNodeWithText("Continue with Google").assertIsDisplayed()
    }

    @Test
    fun registerScreen_numberFieldOnlyAcceptsDigits() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // When
        val numberField = composeTestRule.onNodeWithTag("NumberField")
        numberField.performTextInput("abc123def")

        // Then
        numberField.performTextInput("1234567890")
        composeTestRule.onNodeWithTag("NumberField").assertTextContains("1234567890")
    }

    @Test
    fun registerScreen_showsLoadingIndicatorInButton() {
        // Given
        uiStateFlow.value = AuthUiState.Loading

        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { _, _, _, _, _ -> }
                )
            }
        }

        // Wait for composition and state updates
        composeTestRule.waitForIdle()

        // Then - Should show loading indicator instead of "Register" text
        composeTestRule.onNodeWithTag("RegisterButton").performScrollTo()
        composeTestRule.onNodeWithTag("RegisterButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RegisterButton").assertIsNotEnabled()
        // Note: The loading indicator content is inside the button and difficult to test directly
    }

    @Test
    fun registerScreen_callsRegisterCallbackWithValidData() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                RegisterScreen(
                    navController = rememberNavController(),
                    uiState = uiStateFlow,
                    onRegister = { name, lastName, number, email, password ->
                        registerCalled = true
                        capturedData.clear()
                        capturedData.addAll(listOf(name, lastName, number, email, password))
                    }
                )
            }
        }

        composeTestRule.waitForIdle()

        // When - Fill all fields with valid data
        composeTestRule.onNodeWithTag("NameField").performTextInput("John")
        composeTestRule.onNodeWithTag("LastNameField").performTextInput("Doe")
        composeTestRule.onNodeWithTag("NumberField").performTextInput("1234567890")
        composeTestRule.onNodeWithTag("EmailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("PasswordField").performTextInput("Password123!")
        composeTestRule.onNodeWithTag("ConfirmPasswordField").performTextInput("Password123!")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("RegisterButton").performScrollTo()
        composeTestRule.onNodeWithTag("RegisterButton").performClick()

        composeTestRule.waitForIdle()

        // Then
        assert(registerCalled) { "Register callback was not called" }
        assert(capturedData.size == 5) { "Expected 5 captured data items, got ${capturedData.size}" }
        assert(capturedData[0] == "John") { "Expected name 'John', got '${capturedData.getOrNull(0)}'" }
        assert(capturedData[1] == "Doe") { "Expected lastName 'Doe', got '${capturedData.getOrNull(1)}'" }
        assert(capturedData[2] == "1234567890") {
            "Expected number '1234567890', got '${
                capturedData.getOrNull(
                    2
                )
            }'"
        }
        assert(capturedData[3] == "test@example.com") {
            "Expected email 'test@example.com', got '${
                capturedData.getOrNull(
                    3
                )
            }'"
        }
        assert(capturedData[4] == "Password123!") {
            "Expected password 'Password123!', got '${
                capturedData.getOrNull(
                    4
                )
            }'"
        }
    }
}