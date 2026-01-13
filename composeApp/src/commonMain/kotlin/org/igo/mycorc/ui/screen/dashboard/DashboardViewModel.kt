package org.igo.mycorc.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.usecase.GetNoteListUseCase
import org.igo.mycorc.domain.usecase.SaveNoteUseCase
import kotlin.random.Random
import kotlin.time.ExperimentalTime

// 👇 Внедряем UseCases через конструктор. Koin сам все подставит.
class DashboardViewModel (
    private val getNoteListUseCase: GetNoteListUseCase,
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    // 1. Вышка (Broadcaster + Storage)
    private val _state = MutableStateFlow(DashboardState())
    // 2. Публичная частота (ReadOnly Stream)
    val state: StateFlow<DashboardState> = _state.asStateFlow() //.asStateFlow это типа наследования вышки(!)

    init {
        subscribeToNotes()
    }

    private fun subscribeToNotes() {
        viewModelScope.launch {
            // 👇 Подписываемся на реальный Flow из БД
            getNoteListUseCase().collect { realNotes ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        notes = realNotes
                    )
                }
            }
        }
    }

    // Метод для тестирования записи в БД (вызовем его по кнопке в UI)
    @OptIn(ExperimentalTime::class)
    fun addTestNote() {
        viewModelScope.launch {
            val newNote = Note(
                id = Random.nextLong().toString(), // В реальном проекте используйте UUID
                createdAt = kotlin.time.Clock.System.now(),
                massWeight = Random.nextInt(100, 1000).toDouble(),
                massDescription = "Тестовая партия #${Random.nextInt(1, 99)}",
                status = NoteStatus.DRAFT,
                coalWeight = null,
                isSynced = false
            )
            saveNoteUseCase(newNote)
            // Нам не нужно вручную обновлять _state.notes!
            // SQLDelight сам уведомит Flow, и subscribeToNotes() получит новый список.
        }
    }

    // 👇 ФУНКЦИЯ "ОТПРАВКИ"
    // Пока сервера нет, мы просто меняем статус в БД, чтобы проверить UI
    @OptIn(ExperimentalTime::class)
    fun syncNote(note: Note) {
        viewModelScope.launch {
            // 1. Эмулируем задержку сети (для красоты)
            // kotlinx.coroutines.delay(1000)

            // 2. Обновляем статус на "Отправлено"
            val syncedNote = note.copy(isSynced = true)

            // 3. Сохраняем. SQLDelight сам обновит экран.
            saveNoteUseCase(syncedNote)
        }
    }
}
