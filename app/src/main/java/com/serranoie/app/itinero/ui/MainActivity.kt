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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.theme.ItineroTheme
import com.serranoie.app.itinero.navigation.NavGraph
import com.serranoie.app.itinero.navigation.Route
import com.serranoie.app.itinero.navigation.Screen

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

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
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val startDestination = Route.AppStartNavigation.route

                    NavGraph(navController, startDestination)
                }
            }
        }
    }
}