package org.igo.mycorc.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.igo.mycorc.domain.rep_interface.AuthRepository

// Состояния корневого экрана (Auth Gate)
sealed interface RootState {
    data object Loading : RootState
    data object Authorized : RootState
    data object Unauthorized : RootState
}

class RootViewModel(
    authRepository: AuthRepository
) : ViewModel() {

    // Превращаем поток юзера в поток состояний UI
    val state: StateFlow<RootState> = authRepository.currentUser
        .map { user ->
            if (user != null) RootState.Authorized else RootState.Unauthorized
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RootState.Loading // Сначала всегда показываем загрузку
        )
}
