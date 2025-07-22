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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystemlib.ui.network.NetworkObserver
import com.serranoie.app.designsystemlib.ui.network.NetworkStatusBar
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme
import com.serranoie.app.itinero.navigation.NavGraph
import com.serranoie.core.settings.SettingsViewModel
import com.serranoie.itinero.core.domain.exception.UnauthorizedException
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.repository.AuthRepository
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext

@OptIn(ExperimentalAnimationApi::class, ExperimentalCoroutinesApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val authPreferences: AuthPreferencesRepository by inject()
    private val authRepository: AuthRepository by inject()
    private val travelUseCase: TravelUseCase by inject()
    private val networkObserver: NetworkObserver by inject()
    private val settingsViewModel: SettingsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KoinAndroidContext {
                navController = rememberNavController()

                var isReady by remember { mutableStateOf(false) }
                var startDestination by remember { mutableStateOf("") }
                val isConnected by networkObserver.isConnectedFlow.collectAsState(initial = true)

                splashScreen.setKeepOnScreenCondition { !isReady }

                LaunchedEffect(Unit) {
                    startDestination = determineStartDestination()
                    isReady = true
                }

                val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
                val isMaterialYou by settingsViewModel.isMaterialYouEnabled.collectAsState()

                ItineroTheme(
                    darkTheme = isDarkTheme, materialYou = isMaterialYou
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                        ) {
                            NetworkStatusBar(isConnected = isConnected)
                            if (startDestination.isNotEmpty()) {
                                Box {
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
        }
    }

    private suspend fun determineStartDestination(): String {
        return withContext(Dispatchers.IO) {
            if (!authPreferences.isOnboardingCompleted()) {
                return@withContext Route.AppStartNavigation.route
            }

            val token = authRepository.getAuthToken()
            if (token.isNullOrBlank()) {
                return@withContext Route.AuthNavigation.route
            }

            try {
                validateTokenWithServer()

                Route.WelcomeNavigation.route

            } catch (e: Exception) {
                authRepository.logout()
                Route.AuthNavigation.route
            }
        }
    }

    private suspend fun validateTokenWithServer() {
        try {
            val Result = travelUseCase.getAllTravels()
        } catch (e: UnauthorizedException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                "ITINERO - MainActivity", "Network error during token validation: ${e.message}"
            )
        }
    }
}
