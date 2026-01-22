package org.igo.mycorc.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.igo.mycorc.domain.rep_interface.AuthRepository
import org.igo.mycorc.ui.navigation.Destinations

// Состояния стартового экрана
sealed interface MainState {
    data object Loading : MainState
    data object Authorized : MainState
    data object Unauthorized : MainState
}

class MainViewModel(
    authRepository: AuthRepository
) : ViewModel() {

    // Превращаем поток юзера в поток состояний UI
    val state: StateFlow<MainState> = authRepository.currentUser
        .map { user ->
            if (user != null) MainState.Authorized else MainState.Unauthorized
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainState.Loading // Сначала всегда показываем загрузку
        )

    // Навигационное состояние (сохраняется при смене языка)
    private val _currentRoute = MutableStateFlow(Destinations.DASHBOARD)
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val _selectedNoteId = MutableStateFlow<String?>(null)
    val selectedNoteId: StateFlow<String?> = _selectedNoteId.asStateFlow()

    fun navigateTo(route: String) {
        _currentRoute.value = route
    }

    fun navigateToCreate() {
        _selectedNoteId.value = null
        _currentRoute.value = Destinations.CREATE_NOTE
    }

    fun navigateToEdit(noteId: String) {
        _selectedNoteId.value = noteId
        _currentRoute.value = Destinations.CREATE_NOTE
    }

    fun navigateBack() {
        _selectedNoteId.value = null
        _currentRoute.value = Destinations.DASHBOARD
    }
}
