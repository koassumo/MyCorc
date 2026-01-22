package org.igo.mycorc.domain.rep_interface

import kotlinx.coroutines.flow.StateFlow
import org.igo.mycorc.ui.screen.settings.AppThemeConfig
import org.igo.mycorc.ui.screen.settings.AppLanguageConfig

interface SettingsRepository {
    val themeState: StateFlow<AppThemeConfig>
    fun setTheme(theme: AppThemeConfig)

    val languageState: StateFlow<AppLanguageConfig>
    fun setLanguage(language: AppLanguageConfig)
}