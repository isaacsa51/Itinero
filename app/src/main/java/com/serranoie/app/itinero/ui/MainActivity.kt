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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystem.ui.theme.ItineroTheme
import com.serranoie.app.feature.TravelUiState
import com.serranoie.app.feature.TravelViewModel
import com.serranoie.app.itinero.navigation.NavGraph
import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val authPreferences: AuthPreferences by inject()

    @SuppressLint("RememberReturnType")
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        var isReady = false

        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            ItineroTheme {
                navController = rememberNavController()

                val travelViewModel = koinViewModel<TravelViewModel>()
                val travels by travelViewModel.travels.collectAsState()
                val uiState by travelViewModel.uiState.collectAsState()

                val startDestination = remember {
                    derivedStateOf {
                        when {
                            !authPreferences.isOnboardingCompleted() -> Route.AppStartNavigation.route
                            !authPreferences.isUserLoggedIn() -> Route.AuthNavigation.route
                            uiState is TravelUiState.Success<*> && travels.isNotEmpty() -> Route.WelcomeNavigation.route
                            uiState is TravelUiState.Success<*> -> Route.WelcomeNavigation.route
                            else -> null
                        }
                    }
                }

                // Lógica para cargar viajes si está logeado
                LaunchedEffect(Unit) {
                    if (authPreferences.isUserLoggedIn()) {
                        travelViewModel.getAllTravels()
                    } else {
                        isReady = true
                    }
                }

                // Una vez tengamos respuesta del backend
                LaunchedEffect(uiState) {
                    if (startDestination.value != null) {
                        isReady = true
                    }
                }

                // Evita construir NavGraph hasta que sepamos la ruta inicial
                startDestination.value?.let { start ->
                    NavGraph(
                        navController = navController,
                        startDestination = start
                    )
                }
            }
        }
    }
}
