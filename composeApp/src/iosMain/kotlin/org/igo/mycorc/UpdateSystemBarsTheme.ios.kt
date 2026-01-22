package org.igo.mycorc

import androidx.compose.runtime.Composable
import org.igo.mycorc.ui.screen.settings.AppLanguageConfig

@Composable
actual fun UpdateSystemBarsTheme(isDark: Boolean) {
    // iOS управляет статус-баром автоматически через Info.plist
}

@Composable
actual fun UpdateAppLanguage(language: AppLanguageConfig, content: @Composable () -> Unit) {
    // iOS: Compose Resources использует системную локаль
    // Ручное переключение через Locale.setDefault() не работает на iOS
    // TODO: Для полной поддержки нужна нативная реализация через NSUserDefaults
    content()
}
