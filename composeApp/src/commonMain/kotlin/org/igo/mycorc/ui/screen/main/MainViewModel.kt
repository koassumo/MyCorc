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

    // Стек навигации для правильной обработки "Назад"
    private val navigationStack = mutableListOf(Destinations.DASHBOARD)

    fun navigateTo(route: String) {
        // Если переход на основную вкладку (BottomBar) - очищаем стек
        val mainTabs = listOf(
            Destinations.DASHBOARD,
            Destinations.FACILITIES,
            Destinations.SETTINGS,
            Destinations.PROFILE
        )

        if (route in mainTabs) {
            // Переход на основную вкладку - очищаем стек
            navigationStack.clear()
            navigationStack.add(Destinations.DASHBOARD)  // Dashboard - корневой маршрут
        } else {
            // Переход на подэкран - добавляем текущий маршрут в стек
            if (_currentRoute.value != route) {
                navigationStack.add(_currentRoute.value)
            }
        }

        _currentRoute.value = route
    }

    fun navigateToCreate() {
        _selectedNoteId.value = null
        navigationStack.add(_currentRoute.value)
        _currentRoute.value = Destinations.CREATE_NOTE
    }

    fun navigateToEdit(noteId: String) {
        _selectedNoteId.value = noteId
        navigationStack.add(_currentRoute.value)
        _currentRoute.value = Destinations.CREATE_NOTE
    }

    fun navigateBack() {
        _selectedNoteId.value = null

        // Возвращаемся на предыдущий маршрут из стека
        if (navigationStack.isNotEmpty()) {
            _currentRoute.value = navigationStack.removeLast()
        } else {
            // Если стек пустой, возвращаемся на Dashboard
            _currentRoute.value = Destinations.DASHBOARD
        }
    }
}
