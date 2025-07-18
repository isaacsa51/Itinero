package com.serranoie.app.designsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.serranoie.app.designsystem.screens.ComponentsScreen
import com.serranoie.app.designsystem.screens.ExamplesScreen
import com.serranoie.app.designsystem.screens.HomeScreen
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val screen: @Composable () -> Unit
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ItineroTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    
    val navigationItems = listOf(
        NavigationItem(
            label = "Home",
            icon = Icons.Default.Home,
            screen = { HomeScreen() }
        ),
        NavigationItem(
            label = "Components",
            icon = Icons.Default.Settings,
            screen = { ComponentsScreen() }
        ),
        NavigationItem(
            label = "Examples",
            icon = Icons.Default.Star,
            screen = { ExamplesScreen() }
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            navigationItems[selectedItemIndex].screen()
        }
    }
}
