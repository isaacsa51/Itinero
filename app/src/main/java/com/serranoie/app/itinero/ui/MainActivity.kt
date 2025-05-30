package com.serranoie.app.itinero.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystem.ui.theme.ItineroTheme
import com.serranoie.app.feature.TravelViewModel
import com.serranoie.app.itinero.navigation.NavGraph
import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val authPreferences: AuthPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ItineroTheme {

                val layoutDirection = LocalLayoutDirection.current
                val displayCutout = WindowInsets.displayCutout.asPaddingValues()
                val startPadding = displayCutout.calculateStartPadding(layoutDirection)
                val endPadding = displayCutout.calculateEndPadding(layoutDirection)

                navController = rememberNavController()

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
                    var hasCheckedTrips by remember { mutableStateOf(false) }
                    var shouldCheckTrips by remember { mutableStateOf(false) }

                    // Determine start destination based on user state
                    val startDestination = when {
                        !authPreferences.isOnboardingCompleted() -> Route.AppStartNavigation.route
                        !authPreferences.isUserLoggedIn() -> Route.AuthNavigation.route
                        else -> {
                            shouldCheckTrips = true
                            Route.HomeNavigation.route // Temporary, will redirect based on trips
                        }
                    }

                    // Check trips for logged-in users
                    if (shouldCheckTrips && !hasCheckedTrips) {
                        val travelViewModel = koinViewModel<TravelViewModel>()
                        val travels by travelViewModel.travels.collectAsState()

                        LaunchedEffect(Unit) {
                            travelViewModel.getAllTravels()
                        }

                        LaunchedEffect(travels) {
                            if (travels.isEmpty()) {
                                // No trips - redirect to Welcome screen
                                navController.navigate(Route.WelcomeNavigation.route) {
                                    popUpTo(Route.HomeNavigation.route) { inclusive = true }
                                }
                            }
                            // If user has trips, stay in HomeNavigation (default)
                            hasCheckedTrips = true
                        }
                    }

                    NavGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
