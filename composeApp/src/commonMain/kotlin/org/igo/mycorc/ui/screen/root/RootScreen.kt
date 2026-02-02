package org.igo.mycorc.ui.screen.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.mycorc.ui.screen.login.LoginScreen
import org.igo.mycorc.ui.screen.main.MainScreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Корневой экран приложения (Auth Gate).
 * Выступает в роли чистого диспетчера, который решает что показать пользователю
 * на основе состояния авторизации.
 *
 * Не знает о внутренних зависимостях дочерних компонентов - каждый экран
 * самостоятельно получает свои зависимости (принцип инкапсуляции).
 */
@Composable
fun RootScreen() {
    val viewModel = koinViewModel<RootViewModel>()
    val state by viewModel.state.collectAsState()

    when (state) {
        RootState.Loading -> {
            // Экран загрузки (Splash), пока проверяем авторизацию
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        RootState.Unauthorized -> LoginScreen()
        RootState.Authorized -> MainScreen()
    }
}
