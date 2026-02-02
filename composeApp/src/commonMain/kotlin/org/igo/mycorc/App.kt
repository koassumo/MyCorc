package org.igo.mycorc

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.igo.mycorc.domain.rep_interface.SettingsRepository
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.igo.mycorc.ui.screen.main.MainScreen
import org.igo.mycorc.ui.theme.MyAppTheme
import org.igo.mycorc.ui.screen.settings.AppThemeConfig
import org.igo.mycorc.ui.screen.settings.AppLanguageConfig
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val repository = koinInject<SettingsRepository>() //да прибудет с тобою koin
    val themeConfig by repository.themeState.collectAsState()
    val languageConfig by repository.languageState.collectAsState()

    val useDarkTheme = when (themeConfig) {
        AppThemeConfig.SYSTEM -> isSystemInDarkTheme()
        AppThemeConfig.LIGHT -> false
        AppThemeConfig.DARK -> true
    }

    // key() полностью пересоздаёт всё UI при смене языка
    key(languageConfig) {
        UpdateAppLanguage(languageConfig) {
            MyAppTheme(useDarkTheme = useDarkTheme, languageConfig = languageConfig) {
                // Обновляем цвет иконок статус-бара при смене темы
                UpdateSystemBarsTheme(useDarkTheme)

                // Surface перекрывает фон окна правильным цветом темы (чтоб исключить моргание)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
expect fun UpdateSystemBarsTheme(isDark: Boolean)

@Composable
expect fun UpdateAppLanguage(language: AppLanguageConfig, content: @Composable () -> Unit)