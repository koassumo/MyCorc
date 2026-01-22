package org.igo.mycorc

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import org.igo.mycorc.ui.screen.settings.AppLanguageConfig
import java.util.Locale

@Composable
actual fun UpdateSystemBarsTheme(isDark: Boolean) {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
        val insetsController = WindowInsetsControllerCompat(window, view)

        // Светлая тема = тёмные иконки (true), тёмная тема = светлые иконки (false)
        insetsController.isAppearanceLightStatusBars = !isDark
        insetsController.isAppearanceLightNavigationBars = !isDark
    }
}

// Сохраняем оригинальную системную локаль при старте приложения
private val originalSystemLocale: Locale = Locale.getDefault()

@Composable
actual fun UpdateAppLanguage(language: AppLanguageConfig, content: @Composable () -> Unit) {
    // Устанавливаем локаль синхронно ПЕРЕД вызовом content()
    val newLocale = when (language) {
        AppLanguageConfig.SYSTEM -> originalSystemLocale
        AppLanguageConfig.EN -> Locale.ENGLISH
        AppLanguageConfig.RU -> Locale("ru")
        AppLanguageConfig.DE -> Locale.GERMAN
    }
    Locale.setDefault(newLocale)

    content()
}
