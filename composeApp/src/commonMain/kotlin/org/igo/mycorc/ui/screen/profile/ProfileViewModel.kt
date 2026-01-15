package org.igo.mycorc.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.igo.mycorc.domain.rep_interface.AuthRepository

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Подписываемся на текущего юзера, чтобы отобразить Email
    val currentUser = authRepository.currentUser

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            // Навигацию делать не нужно:
            // MainScreen сам увидит, что user == null, и покажет экран входа.
        }
    }
}
