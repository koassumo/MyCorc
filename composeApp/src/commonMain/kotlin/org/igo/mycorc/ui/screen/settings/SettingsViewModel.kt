package org.igo.mycorc.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.igo.mycorc.domain.rep_interface.SettingsRepository

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    // 1. Простая переменная состояния, которую мы контролируем руками
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        // 2. При создании VM начинаем слушать репозиторий
        viewModelScope.launch {
            repository.themeState.collect { newTheme ->
                // Как только в репозитории что-то изменилось -> обновляем наш State
                _state.update { it.copy(selectedTheme = newTheme) }
            }
        }

        viewModelScope.launch {
            repository.languageState.collect { newLanguage ->
                _state.update { it.copy(selectedLanguage = newLanguage) }
            }
        }
    }

    // 3. Действие пользователя
    fun updateTheme(newTheme: AppThemeConfig) {
        // Просто говорим репозиторию сохранить новое значение.
        // А блок init выше сам "услышит" это изменение и обновит экран.
        repository.setTheme(newTheme)
    }

    fun updateLanguage(newLanguage: AppLanguageConfig) {
        repository.setLanguage(newLanguage)
    }
}

