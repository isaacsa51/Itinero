/*
 * Copyright (c) 2025 Isaac Serrano.
 *
 * File: MainActivity.kt
 * Project: Itinero
 * Module: Itinero.app.main
 *
 * This file belongs to the project: Itinero.
 * Last edited: 03 junio 2025
 */

package com.serranoie.app.itinero.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import com.serranoie.app.itinero.navigation.NavGraph
import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import com.serranoie.itinero.core.data.remote.UnauthorizedHandler
import com.serranoie.itinero.core.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val authPreferences: AuthPreferences by inject()
    private val authRepository: AuthRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set up the auth token clearer for UnauthorizedHandler
        UnauthorizedHandler.setAuthTokenClearer {
            // Run logout on IO dispatcher since it might involve database operations
            kotlinx.coroutines.runBlocking {
                withContext(Dispatchers.IO) {
                    authRepository.logout()
                }
            }
        }

        setContent {
            KoinAndroidContext {
                ItineroTheme {
                    val layoutDirection = LocalLayoutDirection.current
                    val displayCutout = WindowInsets.displayCutout.asPaddingValues()
                    val startPadding = displayCutout.calculateStartPadding(layoutDirection)
                    val endPadding = displayCutout.calculateEndPadding(layoutDirection)

                    navController = rememberNavController()

                    var isReady by remember { mutableStateOf(false) }
                    var startDestination by remember { mutableStateOf("") }

                    splashScreen.setKeepOnScreenCondition { !isReady }

                    // Optimized startup flow with server token validation
                    LaunchedEffect(Unit) {
                        startDestination = determineStartDestination()
                        isReady = true

                        // Listen for logout events from UnauthorizedHandler
                        UnauthorizedHandler.logoutEvents.collect {
                            // Navigate to auth screen when user is automatically logged out
                            navController.navigate(Route.AuthNavigation.route) {
                                // Clear all previous destinations from the back stack
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    start = startPadding,
                                    end = endPadding,
                                    bottom = displayCutout.calculateBottomPadding()
                                )
                            )
                            .imePadding()
                            .fillMaxSize(),
                    ) {
                        if (startDestination.isNotEmpty()) {
                            NavGraph(
                                navController = navController,
                                startDestination = startDestination
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun determineStartDestination(): String {
        return withContext(Dispatchers.IO) {
            // Step 1: Check onboarding completion first
            if (!authPreferences.isOnboardingCompleted()) {
                android.util.Log.d(
                    "ITINERO - MainActivity",
                    "Onboarding not completed, navigating to onboarding"
                )
                return@withContext Route.AppStartNavigation.route
            }

            // Step 2: Check if there's a stored token
            val token = authRepository.getAuthToken()
            if (token.isNullOrBlank()) {
                android.util.Log.d("ITINERO - MainActivity", "No token found, navigating to auth")
                return@withContext Route.AuthNavigation.route
            }

            // Step 3: Validate token with server
            android.util.Log.d("ITINERO - MainActivity", "Token found, validating with server...")

            try {
                // Try to make a simple authenticated request to validate the token
                // This will throw an exception if the token is invalid (401)
                validateTokenWithServer()

                android.util.Log.d(
                    "ITINERO - MainActivity",
                    "Token is valid, navigating to welcome"
                )
                Route.WelcomeNavigation.route

            } catch (e: Exception) {
                android.util.Log.w(
                    "ITINERO - MainActivity",
                    "Token validation failed: ${e.message}"
                )

                // Clear invalid token and navigate to auth
                authRepository.logout()
                Route.AuthNavigation.route
            }
        }
    }

    private suspend fun validateTokenWithServer() {
        // We can use any simple authenticated endpoint to validate the token
        // The 401 handling will be automatic via our UnauthorizedHandler
        try {
            // Try to get user's travels - this requires authentication
            val travelUseCase = org.koin.core.context.GlobalContext.get()
                .get<com.serranoie.itinero.core.domain.usecase.TravelUseCase>()
            val result = travelUseCase.getAllTravels()

            // If we get here without exception, the token is valid
            android.util.Log.d(
                "ITINERO - MainActivity",
                "Token validation successful via travel endpoint"
            )

        } catch (e: com.serranoie.itinero.core.data.remote.UnauthorizedException) {
            // Token is invalid, this will be handled by UnauthorizedHandler
            android.util.Log.w("ITINERO - MainActivity", "Token is unauthorized (401)")
            throw e
        } catch (e: Exception) {
            // Other errors (network, etc.) - we'll consider token valid but with network issues
            android.util.Log.w(
                "ITINERO - MainActivity",
                "Network error during token validation: ${e.message}"
            )
            // Don't throw - assume token is valid but there are network issues
        }
    }
}
