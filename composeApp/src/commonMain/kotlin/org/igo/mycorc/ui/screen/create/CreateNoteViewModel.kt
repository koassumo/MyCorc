package org.igo.mycorc.ui.screen.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.igo.mycorc.data.local.ImageStorage
import org.igo.mycorc.core.time.TimeProvider
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.usecase.CheckServerStatusUseCase
import org.igo.mycorc.domain.usecase.GetNoteByIdUseCase
import org.igo.mycorc.domain.usecase.SaveNoteUseCase
import org.igo.mycorc.domain.usecase.SyncNoteUseCase
import org.igo.mycorc.domain.usecase.SyncSingleNoteUseCase
import kotlin.random.Random
import kotlin.time.ExperimentalTime

data class CreateNoteState(
    val isLoading: Boolean = false,
    val biomassWeight: Double = 0.0,
    val coalWeight: Double = 0.0,
    val description: String = "",
    val isSaved: Boolean = false,
    val photoPath: String? = null,
    val photoUrl: String? = null,
    val showFullscreenPhoto: Boolean = false,
    // Новые поля для режима редактирования
    val editMode: Boolean = false,
    val existingNote: Note? = null,
    val isReadOnly: Boolean = false,
    val errorMessage: String? = null
)

class CreateNoteViewModel(
    private val saveNoteUseCase: SaveNoteUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val syncNoteUseCase: SyncNoteUseCase,
    private val checkServerStatusUseCase: CheckServerStatusUseCase,
    private val syncSingleNoteUseCase: SyncSingleNoteUseCase,
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
            // Сбрасываем старое состояние и показываем загрузку
            _state.update { CreateNoteState(isLoading = true, editMode = true) }

            // Сначала получаем локальный статус
            val localNote = getNoteByIdUseCase(noteId).firstOrNull()

            if (localNote != null) {
                val localStatus = localNote.status
                val lockedStatuses = setOf(NoteStatus.SENT, NoteStatus.APPROVED, NoteStatus.REJECTED)

                // 🔒 ПРОВЕРКА 1: Проверяем статус на сервере при открытии карточки
                val serverStatusResult = checkServerStatusUseCase(noteId)
                serverStatusResult.onSuccess { serverStatus ->
                    if (serverStatus != null && serverStatus in lockedStatuses) {
                        println("🔍 Сервер: $serverStatus, Локально: $localStatus")

                        // КОНФЛИКТ: сервер заблокирован, а локально еще редактируемый
                        if (localStatus !in lockedStatuses) {
                            println("⚠️ КОНФЛИКТ! Пакет заблокирован на сервере, но локально редактируемый")

                            // 🔄 СИНХРОНИЗАЦИЯ: Обновляем локальную версию с сервера
                            val syncResult = syncSingleNoteUseCase(noteId)
                            syncResult.onSuccess {
                                println("✅ Пакет синхронизирован с сервера, UI обновится автоматически")
                            }.onFailure { error ->
                                println("⚠️ Ошибка синхронизации: ${error.message}")
                            }

                            _state.update {
                                it.copy(
                                    errorMessage = "Этот пакет уже отправлен на регистрацию с другого устройства"
                                )
                            }
                        } else {
                            println("✓ Конфликта нет - оба заблокированы, открываем в режиме просмотра")
                        }
                    }
                }.onFailure { error ->
                    println("⚠️ Не удалось проверить статус на сервере: ${error.message}")
                }
            }

            // Загружаем данные ОДИН РАЗ (не подписываемся на изменения)
            val note = getNoteByIdUseCase(noteId).firstOrNull()

            if (note != null) {
                // Read-only только для отправленных на регистрацию (SENT, APPROVED, REJECTED)
                // DRAFT и READY_TO_SEND можно редактировать
                val isReadOnly = note.status !in listOf(NoteStatus.DRAFT, NoteStatus.READY_TO_SEND)

                println("📝 Загружена запись: id=${note.id}, status=${note.status}, isReadOnly=$isReadOnly")
                println("📷 Фото: photoPath=${note.photoPath}, photoUrl=${note.photoUrl}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        editMode = true,
                        existingNote = note,
                        isReadOnly = isReadOnly,
                        biomassWeight = note.massWeight,
                        coalWeight = note.coalWeight ?: 0.0,
                        description = note.massDescription,
                        photoPath = note.photoPath,
                        photoUrl = note.photoUrl
                    )
                }
            } else {
                println("⚠️ Запись с id=$noteId не найдена")
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun saveNote() {
        viewModelScope.launch {
            // Показываем индикатор загрузки во время сохранения
            _state.update { it.copy(isLoading = true) }

            val currentState = _state.value

            // 🔒 ПРОВЕРКА 2: Проверяем статус на сервере перед сохранением
            if (currentState.editMode && currentState.existingNote != null) {
                val localStatus = currentState.existingNote.status
                val lockedStatuses = setOf(NoteStatus.SENT, NoteStatus.APPROVED, NoteStatus.REJECTED)

                val serverStatusResult = checkServerStatusUseCase(currentState.existingNote.id)
                serverStatusResult.onSuccess { serverStatus ->
                    if (serverStatus != null && serverStatus in lockedStatuses) {
                        println("🔍 Сервер: $serverStatus, Локально: $localStatus")

                        // КОНФЛИКТ: сервер заблокирован, а локально еще редактируемый
                        if (localStatus !in lockedStatuses) {
                            println("⚠️ КОНФЛИКТ! Пакет заблокирован на сервере, но локально редактируемый")

                            // 🔄 СИНХРОНИЗАЦИЯ: Обновляем локальную версию с сервера
                            val syncResult = syncSingleNoteUseCase(currentState.existingNote.id)
                            syncResult.onSuccess {
                                println("✅ Пакет синхронизирован с сервера, UI обновится автоматически")
                            }.onFailure { error ->
                                println("⚠️ Ошибка синхронизации: ${error.message}")
                            }

                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Невозможно сохранить: пакет уже отправлен на регистрацию с другого устройства"
                                )
                            }
                            return@launch
                        } else {
                            println("✓ Конфликта нет - оба заблокированы, пропускаем сохранение")
                            _state.update { it.copy(isLoading = false) }
                            return@launch
                        }
                    }
                }.onFailure { error ->
                    println("⚠️ Не удалось проверить статус на сервере: ${error.message}")
                    // Продолжаем сохранение, даже если проверка не удалась (может быть оффлайн)
                }
            }

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

    // Очистить сообщение об ошибке
    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}


