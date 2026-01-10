package org.igo.mycorc.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.igo.mycorc.domain.rep_interface.SettingsRepository
import org.igo.mycorc.ui.screen.settings.AppThemeConfig

class SettingsRepositoryImpl : SettingsRepository {
    private val _themeState = MutableStateFlow(AppThemeConfig.SYSTEM)
    override val themeState: StateFlow<AppThemeConfig> = _themeState.asStateFlow()

    override fun setTheme(theme: AppThemeConfig) {
        _themeState.value = theme
    }
}