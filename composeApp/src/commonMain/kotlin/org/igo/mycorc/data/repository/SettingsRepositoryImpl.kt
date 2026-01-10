package org.igo.mycorc.data.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.igo.mycorc.domain.rep_interface.SettingsRepository
import org.igo.mycorc.ui.screen.settings.AppThemeConfig

// 1. Koin сам подаст сюда объект settings, который мы определили выше
class SettingsRepositoryImpl(
    private val settings: Settings
) : SettingsRepository {

    private val _themeState = MutableStateFlow(getCurrentTheme())
    override val themeState: StateFlow<AppThemeConfig> = _themeState.asStateFlow()

    override fun setTheme(theme: AppThemeConfig) {
        // 2. Сохраняем строку (название Enum) в настройки
        settings.putString(KEY_THEME, theme.name)
        // 3. Обновляем Flow
        _themeState.value = theme
    }

    // Вспомогательная функция для чтения при старте
    private fun getCurrentTheme(): AppThemeConfig {
        // Читаем строку. Если ничего нет — берем SYSTEM
        val savedName = settings.getString(KEY_THEME, AppThemeConfig.SYSTEM.name)
        return try {
            AppThemeConfig.valueOf(savedName)
        } catch (e: Exception) {
            AppThemeConfig.SYSTEM
        }
    }

    companion object {
        private const val KEY_THEME = "app_theme_key"
    }
}