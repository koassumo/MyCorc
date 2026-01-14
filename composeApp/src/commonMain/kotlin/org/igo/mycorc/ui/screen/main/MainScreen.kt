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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.mycorc.ui.common.CommonBottomBar
import org.igo.mycorc.ui.navigation.Destinations
import org.igo.mycorc.ui.navigation.bottomNavItems
import org.igo.mycorc.ui.screen.auth.LoginScreen
import org.igo.mycorc.ui.screen.create.CreateNoteScreen
import org.igo.mycorc.ui.screen.dashboard.DashboardScreen
import org.igo.mycorc.ui.screen.settings.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.igo.mycorc.ui.screen.profile.ProfileScreen

@Composable
fun MainScreen() {
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
            LoginScreen()
        }
        MainState.Authorized -> {
            AuthorizedAppContent()
        }
    }
}

@Composable
fun AuthorizedAppContent() {
    // Храним текущий экран в переменной
    var currentRoute by remember { mutableStateOf(Destinations.DASHBOARD) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute != Destinations.CREATE_NOTE) {
                CommonBottomBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onNavigate = { route -> currentRoute = route }
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
                    onNavigateToCreate = { currentRoute = Destinations.CREATE_NOTE }
                )
                Destinations.FACILITIES -> PlaceholderScreen("Раздел Заводы")
                Destinations.SETTINGS -> SettingsScreen()
                Destinations.PROFILE -> ProfileScreen()
                Destinations.CREATE_NOTE -> CreateNoteScreen(
                    onNavigateBack = { currentRoute = Destinations.DASHBOARD }
                )
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