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
import org.igo.mycorc.domain.usecase.SaveNoteUseCase
import kotlin.random.Random
import kotlin.time.ExperimentalTime

data class CreateNoteState @OptIn(ExperimentalTime::class) constructor(
    val biomassWeight: Double = 500.0,
    val coalWeight: Double = 200.0,
    val description: String = "",
    val isSaved: Boolean = false,

    // 👇 Добавляем поле для временного хранения фото
    val imageBytes: ByteArray? = null
) {
    // В Kotlin Multiplatform массивы байтов сложно сравнивать автоматически,
    // поэтому переопределяем equals/hashCode, чтобы StateFlow работал корректно
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CreateNoteState

        if (biomassWeight != other.biomassWeight) return false
        if (coalWeight != other.coalWeight) return false
        if (description != other.description) return false
        if (isSaved != other.isSaved) return false
        if (imageBytes != null) {
            if (other.imageBytes == null) return false
            if (!imageBytes.contentEquals(other.imageBytes)) return false
        } else if (other.imageBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = biomassWeight.hashCode()
        result = 31 * result + coalWeight.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + isSaved.hashCode()
        result = 31 * result + (imageBytes?.contentHashCode() ?: 0)
        return result
    }
}

class CreateNoteViewModel(
    private val saveNoteUseCase: SaveNoteUseCase,
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

    // 👇 1. Метод, который вызовет UI, когда фото выбрано
    fun onPhotoPicked(bytes: ByteArray) {
        _state.update { it.copy(imageBytes = bytes) }
    }

    // 👇 2. Метод сброса фото (если передумал)
    fun clearPhoto() {
        _state.update { it.copy(imageBytes = null) }
    }

    @OptIn(ExperimentalTime::class)
    fun saveNote() {
        viewModelScope.launch {
            val currentState = _state.value

            // А. Сначала сохраняем фото (если оно есть)
            var savedPhotoPath = ""
            if (currentState.imageBytes != null) {
                // saveImage — это suspend функция, она выполнится в IO потоке (мы это прописали в ImageStorage)
                savedPhotoPath = imageStorage.saveImage(currentState.imageBytes)
            }

            // Б. Создаем объект заметки уже с путем к фото
            val newNote = Note(
                id = Random.nextLong().toString(), // По-хорошему здесь нужен UUID
                createdAt = timeProvider.now(),
                massWeight = currentState.biomassWeight,
                massDescription = currentState.description,
                status = NoteStatus.DRAFT,
                coalWeight = currentState.coalWeight,

                // 👇 Передаем путь.
                // ВАЖНО: Убедись, что в твоем классе Note (Domain) поле называется именно так.
                // В NotePayload мы его уже добавили.
                // Если в Note нет поля photoPath, добавь его (как мы делали в начале).
                photoPath = savedPhotoPath
            )

            saveNoteUseCase(newNote)

            // Сигнализируем UI, что всё готово и можно уходить назад
            _state.update { it.copy(isSaved = true) }
        }
    }

    // Сбрасываем форму в исходное состояние
    fun resetState() {
        _state.update { CreateNoteState() }
    }
}


