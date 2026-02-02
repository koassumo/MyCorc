package org.igo.mycorc.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.igo.mycorc.ui.common.CommonBottomBar
import org.igo.mycorc.ui.common.PlaceholderScreen
import org.igo.mycorc.ui.navigation.Destinations
import org.igo.mycorc.ui.navigation.rememberBottomNavItems
import org.igo.mycorc.ui.screen.create.CreateNoteScreen
import org.igo.mycorc.ui.screen.dashboard.DashboardScreen
import org.igo.mycorc.ui.screen.profile.ProfileScreen
import org.igo.mycorc.ui.screen.settings.SettingsScreen
import org.igo.mycorc.ui.theme.LocalAppStrings
import org.koin.compose.viewmodel.koinViewModel

/**
 * Главный экран приложения для авторизованного пользователя.
 * Содержит Scaffold с нижней навигацией и управляет переключением между экранами.
 *
 * Инкапсулирует получение MainViewModel через Koin, чтобы родительский компонент
 * (RootScreen) не знал о внутренних зависимостях.
 */
@Composable
fun MainScreen() {
    // Получаем ViewModel внутри компонента (инкапсуляция)
    val viewModel = koinViewModel<MainViewModel>()
    val strings = LocalAppStrings.current
    val bottomNavItems = rememberBottomNavItems()

    // Навигационное состояние из ViewModel (сохраняется при смене языка)
    val currentRoute by viewModel.currentRoute.collectAsState()
    val selectedNoteId by viewModel.selectedNoteId.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        bottomBar = {
            if (currentRoute != Destinations.CREATE_NOTE) {
                CommonBottomBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                Destinations.DASHBOARD -> DashboardScreen(
                    onNavigateToCreate = { viewModel.navigateToCreate() },
                    onNavigateToEdit = { noteId -> viewModel.navigateToEdit(noteId) }
                )
                Destinations.FACILITIES -> PlaceholderScreen(strings.facilitiesSection)
                Destinations.SETTINGS -> SettingsScreen()
                Destinations.PROFILE -> ProfileScreen()
                Destinations.CREATE_NOTE -> CreateNoteScreen(
                    noteId = selectedNoteId,
                    onNavigateBack = { viewModel.navigateBack() }
                )
            }
        }
    }
}
