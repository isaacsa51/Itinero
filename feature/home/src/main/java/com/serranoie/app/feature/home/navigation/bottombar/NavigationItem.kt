package com.serranoie.app.feature.onboard.navigation.bottombar

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)