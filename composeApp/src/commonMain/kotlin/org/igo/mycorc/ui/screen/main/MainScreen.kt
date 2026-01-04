package org.igo.mycorc.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.mycorc.ui.common.CommonBottomBar
import org.igo.mycorc.ui.navigation.Destinations
import org.igo.mycorc.ui.navigation.bottomNavItems
import org.igo.mycorc.ui.screen.dashboard.DashboardScreen
import org.igo.mycorc.ui.screen.settings.SettingsScreen

@Composable
fun MainScreen() {
    // Храним текущий экран в переменной (простейшая навигация)
    var currentRoute by remember { mutableStateOf(Destinations.DASHBOARD) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            CommonBottomBar(
                items = bottomNavItems,
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route }
            )
        }
    ) { innerPadding ->

        // Контейнер для экранов
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Учитываем высоту нижней панели
        ) {
            when (currentRoute) {
                Destinations.DASHBOARD -> DashboardScreen()
                Destinations.FACILITIES -> PlaceholderScreen("Раздел Заводы")
                Destinations.SETTINGS -> SettingsScreen()
                Destinations.PROFILE -> PlaceholderScreen("Личный кабинет")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}