package org.igo.mycorc.ui.screen.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.igo.mycorc.ui.navigation.Destinations

class MainViewModel : ViewModel() {

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
