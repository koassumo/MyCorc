package org.igo.mycorc.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)

// Наш список кнопок. Легко добавить новую здесь.
val bottomNavItems = listOf(
    BottomNavItem("Партии", Destinations.DASHBOARD, Icons.Default.Home),
    BottomNavItem("Заводы", Destinations.FACILITIES, Icons.Default.Factory),
    BottomNavItem("Settings", Destinations.SETTINGS, Icons.Default.Settings),
    BottomNavItem("Профиль", Destinations.PROFILE, Icons.Default.AccountCircle)
)