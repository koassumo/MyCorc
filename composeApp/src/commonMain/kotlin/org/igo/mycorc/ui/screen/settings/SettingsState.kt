package org.igo.mycorc.ui.screen.settings

// 1. Перечисление наших вариантов
enum class AppThemeConfig {
    SYSTEM, // Как в системе (по умолчанию)
    LIGHT,  // Всегда светлая
    DARK    // Всегда темная
}

// 2. Состояние экрана.
// Пока храним только выбранную тему. Позже сюда добавятся уведомления, язык и т.д.
data class SettingsState(
    val selectedTheme: AppThemeConfig = AppThemeConfig.SYSTEM
)