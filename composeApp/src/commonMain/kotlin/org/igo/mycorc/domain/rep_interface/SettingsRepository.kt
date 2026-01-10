package org.igo.mycorc.domain.rep_interface

import kotlinx.coroutines.flow.StateFlow
import org.igo.mycorc.ui.screen.settings.AppThemeConfig

interface SettingsRepository {
    val themeState: StateFlow<AppThemeConfig>
    fun setTheme(theme: AppThemeConfig)
}