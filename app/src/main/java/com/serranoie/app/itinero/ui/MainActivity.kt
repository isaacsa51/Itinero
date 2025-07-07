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

import android.os.Bundle
import android.util.Log
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
import com.serranoie.itinero.core.domain.exception.UnauthorizedException
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val authPreferences: AuthPreferencesRepository by inject()
    private val authRepository: AuthRepository by inject()
    private val travelUseCase: TravelUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


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

                    LaunchedEffect(Unit) {
                        startDestination = determineStartDestination()
                        isReady = true
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
                                navController = navController, startDestination = startDestination
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun determineStartDestination(): String {
        return withContext(Dispatchers.IO) {
            if (!authPreferences.isOnboardingCompleted()) {
                Log.d(
                    "ITINERO - MainActivity", "Onboarding not completed, navigating to onboarding"
                )
                return@withContext Route.AppStartNavigation.route
            }

            val token = authRepository.getAuthToken()
            if (token.isNullOrBlank()) {
                Log.d("ITINERO - MainActivity", "No token found, navigating to auth")
                return@withContext Route.AuthNavigation.route
            }

            Log.d("ITINERO - MainActivity", "Token found, validating with server...")

            try {
                validateTokenWithServer()

                Log.d(
                    "ITINERO - MainActivity", "Token is valid, navigating to welcome"
                )
                Route.WelcomeNavigation.route

            } catch (e: Exception) {
                Log.e(
                    "ITINERO - MainActivity", "Token validation failed: ${e.message}"
                )

                authRepository.logout()
                Route.AuthNavigation.route
            }
        }
    }

    private suspend fun validateTokenWithServer() {
        try {
            val Result = travelUseCase.getAllTravels()

            Log.d(
                "ITINERO - MainActivity", "Token validation successful via travel endpoint"
            )

        } catch (e: UnauthorizedException) {
            Log.e("ITINERO - MainActivity", "Token is unauthorized (401)")
            throw e
        } catch (e: Exception) {
            Log.e(
                "ITINERO - MainActivity", "Network error during token validation: ${e.message}"
            )
        }
    }
}
