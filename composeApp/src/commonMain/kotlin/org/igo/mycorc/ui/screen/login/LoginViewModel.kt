package org.igo.mycorc.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.igo.mycorc.domain.rep_interface.AuthRepository

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = authRepository.login(email, pass)

            _isLoading.value = false
            result.onFailure {
                _error.value = it.message ?: "Ошибка входа"
            }
            // onSuccess ничего делать не надо, MainScreen сам переключится
        }
    }

    fun register(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = authRepository.register(email, pass)
            _isLoading.value = false
            result.onFailure { _error.value = it.message }
        }
    }

    fun signInWithGoogle(activityContext: Any) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = authRepository.signInWithGoogle(activityContext)

            _isLoading.value = false
            result.onFailure {
                _error.value = it.message ?: "Ошибка входа через Google"
            }
            // onSuccess ничего делать не надо, MainScreen сам переключится
        }
    }
}
