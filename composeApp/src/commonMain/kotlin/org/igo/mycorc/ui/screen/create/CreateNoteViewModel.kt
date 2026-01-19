package org.igo.mycorc.ui.screen.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.igo.mycorc.data.local.ImageStorage
import org.igo.mycorc.core.time.TimeProvider
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.usecase.GetNoteByIdUseCase
import org.igo.mycorc.domain.usecase.SaveNoteUseCase
import org.igo.mycorc.domain.usecase.SyncNoteUseCase
import kotlin.random.Random
import kotlin.time.ExperimentalTime

data class CreateNoteState(
    val biomassWeight: Double = 500.0,
    val coalWeight: Double = 200.0,
    val description: String = "",
    val isSaved: Boolean = false,
    val photoPath: String? = null,
    val showFullscreenPhoto: Boolean = false,
    // Новые поля для режима редактирования
    val editMode: Boolean = false,
    val existingNote: Note? = null,
    val isReadOnly: Boolean = false
)

class CreateNoteViewModel(
    private val saveNoteUseCase: SaveNoteUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val syncNoteUseCase: SyncNoteUseCase,
    private val imageStorage: ImageStorage,
    private val timeProvider: TimeProvider
) : ViewModel() {

    private val _state = MutableStateFlow(CreateNoteState())
    val state = _state.asStateFlow()

    fun updateBiomass(value: Double) {
        _state.update { it.copy(biomassWeight = value) }
    }

    fun updateCoal(value: Double) {
        _state.update { it.copy(coalWeight = value) }
    }

    fun updateDescription(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onPhotoPicked(bytes: ByteArray) {
        viewModelScope.launch {
            val path = imageStorage.saveImage(bytes)
            _state.update { it.copy(photoPath = path) }
        }
    }

    fun clearPhoto() {
        viewModelScope.launch {
            _state.value.photoPath?.let { imageStorage.deleteImage(it) }
            _state.update { it.copy(photoPath = null) }
        }
    }

    fun openFullscreenPhoto() {
        _state.update { it.copy(showFullscreenPhoto = true) }
    }

    fun closeFullscreenPhoto() {
        _state.update { it.copy(showFullscreenPhoto = false) }
    }

    // Загрузить существующую запись для редактирования
    fun loadNote(noteId: String) {
        viewModelScope.launch {
            getNoteByIdUseCase(noteId).collect { note ->
                if (note != null) {
                    println("📝 Загружена запись для редактирования: ${note.id}, isSynced=${note.isSynced}")
                    _state.update {
                        it.copy(
                            editMode = true,
                            existingNote = note,
                            isReadOnly = note.isSynced,
                            biomassWeight = note.massWeight,
                            coalWeight = note.coalWeight ?: 200.0,
                            description = note.massDescription,
                            photoPath = note.photoPath
                        )
                    }
                } else {
                    println("⚠️ Запись с id=$noteId не найдена")
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun saveNote() {
        viewModelScope.launch {
            val currentState = _state.value

            // Проверяем, заполнены ли ВСЕ обязательные поля
            val isComplete = currentState.biomassWeight > 0 &&
                    currentState.description.isNotEmpty() &&
                    currentState.coalWeight != null &&
                    currentState.coalWeight!! > 0 &&
                    currentState.photoPath != null

            // Определяем статус: READY_TO_SEND если все заполнено, иначе DRAFT
            val newStatus = if (isComplete) {
                NoteStatus.READY_TO_SEND
            } else {
                NoteStatus.DRAFT
            }

            println("📋 Проверка полей: isComplete=$isComplete, newStatus=$newStatus")

            val note = if (currentState.editMode && currentState.existingNote != null) {
                // ОБНОВЛЕНИЕ существующей записи
                println("💾 Обновление существующей записи: ${currentState.existingNote.id}")

                // Если запись уже отправлена (SENT), не меняем статус
                val finalStatus = if (currentState.existingNote.status == NoteStatus.SENT) {
                    NoteStatus.SENT
                } else {
                    newStatus
                }

                currentState.existingNote.copy(
                    massWeight = currentState.biomassWeight,
                    massDescription = currentState.description,
                    coalWeight = currentState.coalWeight,
                    photoPath = currentState.photoPath,
                    status = finalStatus
                )
            } else {
                // СОЗДАНИЕ новой записи
                println("✨ Создание новой записи")
                Note(
                    id = Random.nextLong().toString(),
                    createdAt = timeProvider.now(),
                    massWeight = currentState.biomassWeight,
                    massDescription = currentState.description,
                    status = newStatus,
                    coalWeight = currentState.coalWeight,
                    photoPath = currentState.photoPath
                )
            }

            saveNoteUseCase(note)
            println("💾 Запись сохранена локально")

            // Автоматическая синхронизация на сервер (черновик, не меняем статус на SENT)
            val syncResult = syncNoteUseCase(note, markAsSent = false)
            syncResult.onSuccess {
                println("☁️ Автосинхронизация успешна: запись отправлена на сервер")
            }.onFailure { error ->
                println("⚠️ Ошибка автосинхронизации: ${error.message}")
                error.printStackTrace()
                // Не блокируем сохранение, если синхронизация не удалась
            }

            _state.update { it.copy(isSaved = true) }
        }
    }

    // Сбрасываем форму в исходное состояние
    fun resetState() {
        _state.update { CreateNoteState() }
    }
}


