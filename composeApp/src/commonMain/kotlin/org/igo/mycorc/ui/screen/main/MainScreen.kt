package org.igo.mycorc.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.mycorc.ui.common.CommonBottomBar
import org.igo.mycorc.ui.common.CommonTopBar
import org.igo.mycorc.ui.navigation.Destinations
import org.igo.mycorc.ui.navigation.rememberBottomNavItems
import org.igo.mycorc.ui.screen.auth.LoginScreen
import org.igo.mycorc.ui.screen.create.CreateNoteScreen
import org.igo.mycorc.ui.screen.dashboard.DashboardScreen
import org.igo.mycorc.ui.screen.settings.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.igo.mycorc.ui.screen.profile.ProfileScreen
import org.igo.mycorc.ui.theme.LocalAppStrings

@Composable
fun MainScreen(activityContext: Any? = null) {
    // Теперь мы используем ViewModel, а не лезем в репозиторий напрямую
    val viewModel = koinViewModel<MainViewModel>()
    val state by viewModel.state.collectAsState()

    when (state) {
        MainState.Loading -> {
            // Экран загрузки (Splash), пока проверяем авторизацию
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        MainState.Unauthorized -> {
            LoginScreen(activityContext = activityContext)
        }
        MainState.Authorized -> {
            AuthorizedAppContent(viewModel)
        }
    }
}

@Composable
fun AuthorizedAppContent(viewModel: MainViewModel) {
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

@Composable
fun PlaceholderScreen(title: String) {
    Scaffold(
        topBar = { CommonTopBar(title = title) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
        }
    }
}