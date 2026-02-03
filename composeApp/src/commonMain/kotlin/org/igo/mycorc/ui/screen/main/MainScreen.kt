package org.igo.mycorc.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.igo.mycorc.ui.common.AppBackHandler
import org.igo.mycorc.ui.common.CommonBottomBar
import org.igo.mycorc.ui.common.CommonTopBar
import org.igo.mycorc.ui.common.ExitDialog
import org.igo.mycorc.ui.common.LocalTopBarState
import org.igo.mycorc.ui.common.PlaceholderScreen
import org.igo.mycorc.ui.common.TopBarState
import org.igo.mycorc.ui.common.exitApp
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
 * Единый Scaffold: TopBar, BottomBar, FAB, Snackbar.
 * Дочерние экраны публикуют конфигурацию TopBar через LocalTopBarState.
 */
@Composable
fun MainScreen() {
    val viewModel = koinViewModel<MainViewModel>()
    val strings = LocalAppStrings.current
    val bottomNavItems = rememberBottomNavItems()

    val currentRoute by viewModel.currentRoute.collectAsState()
    val selectedNoteId by viewModel.selectedNoteId.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val topBarState = remember { TopBarState() }

    // Обработчик физической кнопки "Назад" (Android)
    AppBackHandler(enabled = true) {
        if (currentRoute == Destinations.DASHBOARD) {
            showExitDialog = true
        } else {
            viewModel.navigateBack()
        }
    }

    CompositionLocalProvider(LocalTopBarState provides topBarState) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CommonTopBar(
                    title = topBarState.title,
                    canNavigateBack = topBarState.canNavigateBack,
                    navigateUp = topBarState.onNavigateBack,
                    backButtonDescription = strings.backButtonTooltip
                )
            },
            floatingActionButton = {
                if (currentRoute == Destinations.DASHBOARD) {
                    FloatingActionButton(
                        onClick = { viewModel.navigateToCreate() },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = strings.addButtonTooltip)
                    }
                }
            },
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
                        onNavigateToEdit = { noteId -> viewModel.navigateToEdit(noteId) },
                        snackbarHostState = snackbarHostState
                    )
                    Destinations.FACILITIES -> PlaceholderScreen()
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

    // Диалог подтверждения выхода
    ExitDialog(
        showDialog = showExitDialog,
        onDismiss = { showExitDialog = false },
        onConfirmExit = { exitApp() }
    )
}
