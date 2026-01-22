package org.igo.mycorc

import androidx.compose.runtime.Composable
import org.igo.mycorc.ui.screen.settings.AppLanguageConfig
import java.util.Locale

@Composable
actual fun UpdateSystemBarsTheme(isDark: Boolean) {
    // Desktop не имеет системных баров
}

// Сохраняем оригинальную системную локаль при старте приложения
private val originalSystemLocale: java.util.Locale = java.util.Locale.getDefault()

@Composable
actual fun UpdateAppLanguage(language: AppLanguageConfig, content: @Composable () -> Unit) {
    // Устанавливаем локаль синхронно ПЕРЕД вызовом content()
    val newLocale = when (language) {
        AppLanguageConfig.SYSTEM -> originalSystemLocale
        AppLanguageConfig.EN -> java.util.Locale.ENGLISH
        AppLanguageConfig.RU -> java.util.Locale("ru")
        AppLanguageConfig.DE -> java.util.Locale.GERMAN
    }
    java.util.Locale.setDefault(newLocale)

    content()
}
