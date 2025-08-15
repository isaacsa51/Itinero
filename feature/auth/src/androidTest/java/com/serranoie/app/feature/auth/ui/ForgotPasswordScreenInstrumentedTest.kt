package com.serranoie.app.feature.auth.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForgotPasswordScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun forgotPasswordScreen_displaysAllUIElements() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ForgotPasswordScreen(navController = rememberNavController())
            }
        }

        // Then
        composeTestRule.onNodeWithTag("forgotPasswordImage").assertIsDisplayed()
        composeTestRule.onNodeWithTag("recoverPasswordTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("forgotPasswordSubTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("forgotPasswordHelpText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("forgotPasswordEmailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("forgotPasswordRecoverButton").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordScreen_displaysTitleAndLabels() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ForgotPasswordScreen(navController = rememberNavController())
            }
        }

        // Then
        composeTestRule.onNodeWithText("Recover your password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Remember to enter the email address associated with your account.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("This will help you receive a password recovery email with instructions on how to reset your password.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Recover account").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordScreen_recoverButtonIsEnabledAndClickable() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ForgotPasswordScreen(navController = rememberNavController())
            }
        }

        // Then
        composeTestRule.onNodeWithTag("forgotPasswordRecoverButton")
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun forgotPasswordScreen_emailFieldAcceptsInput() {
        // Given
        val testEmail = "test@example.com"

        composeTestRule.setContent {
            ItineroTheme {
                ForgotPasswordScreen(navController = rememberNavController())
            }
        }

        // When
        composeTestRule.onNodeWithTag("forgotPasswordEmailField").performTextInput(testEmail)

        // Then
        composeTestRule.onNodeWithTag("forgotPasswordEmailField").assertTextContains(testEmail)
    }

    // TODO: Uncomment when correct functionality is added
//    @Test
//    fun forgotPasswordScreen_recoverButtonTriggersAction() {
//        // Given
//        composeTestRule.setContent {
//            ItineroTheme {
//                ForgotPasswordScreen(navController = rememberNavController())
//            }
//        }
//
//        // When
//        composeTestRule.onNodeWithTag("forgotPasswordRecoverButton").performClick()
//
//        // Then - Button click should be successful
//        composeTestRule.onNodeWithTag("forgotPasswordRecoverButton").assertIsDisplayed()
//    }

    @Test
    fun forgotPasswordScreen_emailFieldHasCorrectPlaceholder() {
        // Given
        composeTestRule.setContent {
            ItineroTheme {
                ForgotPasswordScreen(navController = rememberNavController())
            }
        }

        // Then
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    }
}