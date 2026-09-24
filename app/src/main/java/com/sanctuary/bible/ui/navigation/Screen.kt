package com.sanctuary.bible.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Read : Screen("read", "Read", Icons.Default.AutoStories)
    object Plan : Screen("plan", "Plan", Icons.Default.EventAvailable)
    object Progress : Screen("progress", "Progress", Icons.Default.BarChart)
    object More : Screen("more", "More", Icons.Default.GridView)

    companion object {
        val bottomNavScreens: List<Screen>
            get() = listOf(Home, Read, Plan, Progress, More)
    }
}
