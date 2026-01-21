package org.igo.mycorc.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.igo.mycorc.ui.theme.LocalAppStrings

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)

// Composable функция для получения локализированного списка кнопок
@Composable
fun rememberBottomNavItems(): List<BottomNavItem> {
    val strings = LocalAppStrings.current
    return listOf(
        BottomNavItem(strings.packagesNav, Destinations.DASHBOARD, Icons.Default.Home),
        BottomNavItem(strings.facilitiesNav, Destinations.FACILITIES, Icons.Default.Factory),
        BottomNavItem(strings.settingsNav, Destinations.SETTINGS, Icons.Default.Settings),
        BottomNavItem(strings.profileNav, Destinations.PROFILE, Icons.Default.AccountCircle)
    )
}

// Старая версия для обратной совместимости (deprecated)
@Deprecated("Use rememberBottomNavItems() instead for localization support")
val bottomNavItems = listOf(
    BottomNavItem("Партии", Destinations.DASHBOARD, Icons.Default.Home),
    BottomNavItem("Заводы", Destinations.FACILITIES, Icons.Default.Factory),
    BottomNavItem("Settings", Destinations.SETTINGS, Icons.Default.Settings),
    BottomNavItem("Профиль", Destinations.PROFILE, Icons.Default.AccountCircle)
)