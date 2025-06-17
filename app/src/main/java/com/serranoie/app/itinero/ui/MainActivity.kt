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
import androidx.compose.runtime.derivedStateOf
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
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val authPreferences: AuthPreferences by inject()

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

                    splashScreen.setKeepOnScreenCondition { !isReady }

                    val startDestination = remember {
                        derivedStateOf {
                            when {
                                !authPreferences.isOnboardingCompleted() -> Route.AppStartNavigation.route
                                !authPreferences.isUserLoggedIn() -> Route.AuthNavigation.route
                                else -> Route.WelcomeNavigation.route
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
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
                        NavGraph(
                            navController = navController, startDestination = startDestination.value
                        )
                    }
                }
            }
        }
    }
}
