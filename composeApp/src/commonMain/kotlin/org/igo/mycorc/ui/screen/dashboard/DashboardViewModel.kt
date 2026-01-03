package org.igo.mycorc.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NoteStatus
import kotlin.time.ExperimentalTime

class DashboardViewModel : ViewModel() {

    // 1. Вышка (Broadcaster + Storage)
    private val _state = MutableStateFlow(DashboardState())
    // 2. Публичная частота (ReadOnly Stream)
    val state: StateFlow<DashboardState> = _state.asStateFlow() //.asStateFlow это типа наследования вышки(!)

    init {
        loadNotes()
    }

    @OptIn(ExperimentalTime::class)
    private fun loadNotes() {
        viewModelScope.launch {
            // Показываем крутилку
            _state.update { it.copy(isLoading = true) }

            // Имитация задержки сети (чтобы ты увидел загрузку)
            delay(1000)

            // Генерируем фейковые данные для теста
            val dummyNotes = List(5) { index ->
                Note(
                    id = index.toString(),
                    createdAt = Clock.System.now(),
                    massWeight = 500.0 + (index * 50),
                    massValue = 10.0,
                    massDescription = "Дуб, партия №${index + 1}",
                    status = NoteStatus.DRAFT,
                    // Добавим уголь только для четных, чтобы проверить условие в UI
                    coalWeight = if (index % 2 == 0) 200.0 else null
                )
            }

            // Обновляем состояние (убираем крутилку, кладем данные)
            _state.update {
                it.copy(
                    isLoading = false,
                    notes = dummyNotes
                )
            }
        }
    }
}
