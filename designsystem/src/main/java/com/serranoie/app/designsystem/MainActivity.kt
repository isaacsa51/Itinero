package com.serranoie.app.designsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.preferencesDataStore
import com.serranoie.app.designsystem.screens.ComponentsScreen
import com.serranoie.app.designsystem.screens.ExamplesScreen
import com.serranoie.app.designsystem.screens.HomeScreen
import com.serranoie.app.designsystemlib.ui.theme.ItineroTheme

private val android.content.Context.dataStore by preferencesDataStore("theme_preferences")

data class NavigationItem(
    val label: String, val icon: ImageVector, val screen: @Composable () -> Unit
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    // Initialize ViewModel with DataStore
    val context = LocalContext.current
    val themeViewModel = remember { ThemeViewModel(context.dataStore) }

    // Collect theme states from ViewModel
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)
    val useMaterialYou by themeViewModel.isMaterialYou.collectAsState(initial = false)

    val navigationItems = listOf(
        NavigationItem(
            label = "Home", icon = Icons.Default.Home, screen = { HomeScreen() }),
        NavigationItem(
            label = "Components", icon = Icons.Default.Settings, screen = { ComponentsScreen() }),
        NavigationItem(
            label = "Examples", icon = Icons.Default.Star, screen = { ExamplesScreen() }))

    ItineroTheme(
        darkTheme = isDarkTheme,
        materialYou = useMaterialYou
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = {
                Text("Itinero Design System")
            }, actions = {
                // Material You Toggle
                IconButton(
                    onClick = { themeViewModel.toggleMaterialYou(!useMaterialYou) }) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = if (!useMaterialYou) "Switch to Static Theme" else "Switch to Material You",
                        tint = if (useMaterialYou) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Dark/Light Mode Toggle
                IconButton(
                    onClick = { themeViewModel.toggleTheme(!isDarkTheme) }) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode"
                    )
                }
            })
        }, bottomBar = {
            NavigationBar {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) })
                }
            }
        }) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                navigationItems[selectedItemIndex].screen()
            }
        }
    }
}
