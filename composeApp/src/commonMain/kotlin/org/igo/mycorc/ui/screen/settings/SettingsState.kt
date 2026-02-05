package org.igo.mycorc.ui.screen.settings

// 1. Перечисление наших вариантов
enum class AppThemeConfig {
    SYSTEM,  // Как в системе (по умолчанию)
    LIGHT,   // Светлая тема
    DARK     // Тёмная тема
}

// Перечисление языков
enum class AppLanguageConfig {
    SYSTEM, // Как в системе (по умолчанию)
    EN,     // Английский
    RU,     // Русский
    DE      // Немецкий
}

// 2. Состояние экрана.
data class SettingsState(
    val selectedTheme: AppThemeConfig = AppThemeConfig.SYSTEM,
    val selectedLanguage: AppLanguageConfig = AppLanguageConfig.SYSTEM
)