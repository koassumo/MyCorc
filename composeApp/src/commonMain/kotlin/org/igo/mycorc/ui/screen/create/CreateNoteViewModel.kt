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

data class CreateNoteState(
    val biomassWeight: Double = 500.0,
    val coalWeight: Double = 200.0,
    val description: String = "",
    val isSaved: Boolean = false,
    val photoPath: String? = null,
    val showFullscreenPhoto: Boolean = false
)

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

    @OptIn(ExperimentalTime::class)
    fun saveNote() {
        viewModelScope.launch {
            val currentState = _state.value

            val newNote = Note(
                id = Random.nextLong().toString(),
                createdAt = timeProvider.now(),
                massWeight = currentState.biomassWeight,
                massDescription = currentState.description,
                status = NoteStatus.DRAFT,
                coalWeight = currentState.coalWeight,
                photoPath = currentState.photoPath
            )

            saveNoteUseCase(newNote)

            _state.update { it.copy(isSaved = true) }
        }
    }

    // Сбрасываем форму в исходное состояние
    fun resetState() {
        _state.update { CreateNoteState() }
    }
}


