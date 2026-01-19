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
import org.igo.mycorc.domain.usecase.CheckServerStatusUseCase
import org.igo.mycorc.domain.usecase.GetNoteListUseCase
import org.igo.mycorc.domain.usecase.SaveNoteUseCase
import org.igo.mycorc.domain.usecase.SyncFromServerUseCase
import org.igo.mycorc.domain.usecase.SyncSingleNoteUseCase
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import org.igo.mycorc.domain.usecase.SyncNoteUseCase

// 👇 Внедряем UseCases через конструктор. Koin сам все подставит.
class DashboardViewModel (
    private val getNoteListUseCase: GetNoteListUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val syncNoteUseCase: SyncNoteUseCase,
    private val syncFromServerUseCase: SyncFromServerUseCase,
    private val checkServerStatusUseCase: CheckServerStatusUseCase,
    private val syncSingleNoteUseCase: SyncSingleNoteUseCase
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

    // 👇 ФУНКЦИЯ "ОТПРАВКИ НА РЕГИСТРАЦИЮ"
    @OptIn(ExperimentalTime::class)
    fun syncNote(note: Note) {
        viewModelScope.launch {
            val localStatus = note.status
            val lockedStatuses = setOf(NoteStatus.SENT, NoteStatus.APPROVED, NoteStatus.REJECTED)

            // 🔒 ПРОВЕРКА 3: Проверяем статус на сервере перед отправкой на регистрацию
            val serverStatusResult = checkServerStatusUseCase(note.id)
            serverStatusResult.onSuccess { serverStatus ->
                if (serverStatus != null && serverStatus in lockedStatuses) {
                    println("🔍 Сервер: $serverStatus, Локально: $localStatus")

                    // КОНФЛИКТ: сервер заблокирован, а локально еще редактируемый
                    if (localStatus !in lockedStatuses) {
                        println("⚠️ КОНФЛИКТ! Пакет заблокирован на сервере, но локально редактируемый")

                        // 🔄 СИНХРОНИЗАЦИЯ: Обновляем локальную версию с сервера
                        val syncResult = syncSingleNoteUseCase(note.id)
                        syncResult.onSuccess {
                            println("✅ Пакет синхронизирован с сервера, UI обновится автоматически")
                        }.onFailure { error ->
                            println("⚠️ Ошибка синхронизации: ${error.message}")
                        }

                        _state.update {
                            it.copy(errorMessage = "Этот пакет уже отправлен на регистрацию с другого устройства")
                        }
                        return@launch
                    } else {
                        println("✓ Конфликта нет - уже заблокирован, пропускаем отправку")
                        return@launch
                    }
                }
            }.onFailure { error ->
                println("⚠️ Не удалось проверить статус на сервере: ${error.message}")
                // Продолжаем отправку, даже если проверка не удалась (может быть оффлайн)
            }

            // Финальная отправка - меняем статус на SENT
            val result = syncNoteUseCase(note, markAsSent = true)
            result.onSuccess {
                println("✅ Отправка на регистрацию успешна: noteId=${note.id}, статус=SENT")
            }.onFailure { error ->
                println("❌ Ошибка отправки на регистрацию: ${error.message}")
                error.printStackTrace()
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // 👇 ФУНКЦИЯ "СИНХРОНИЗАЦИЯ С СЕРВЕРА"
    fun syncFromServer() {
        viewModelScope.launch {
            _state.update { it.copy(isSyncing = true) }
            val result = syncFromServerUseCase()
            result.onSuccess {
                println("✅ Синхронизация с сервера завершена успешно")
                _state.update { it.copy(isSyncing = false) }
            }.onFailure { error ->
                println("❌ Ошибка синхронизации с сервера: ${error.message}")
                error.printStackTrace()
                _state.update { it.copy(isSyncing = false) }
            }
        }
    }
}
