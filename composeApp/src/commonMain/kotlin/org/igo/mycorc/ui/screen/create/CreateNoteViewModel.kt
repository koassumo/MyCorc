package org.igo.mycorc.ui.screen.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.usecase.SaveNoteUseCase
import kotlin.random.Random
import kotlin.time.ExperimentalTime

data class CreateNoteState @OptIn(ExperimentalTime::class) constructor(
    val biomassWeight: Double = 500.0, // Дефолтное значение
    val coalWeight: Double = 200.0,
    val description: String = "Партия от ${kotlin.time.Clock.System.now().epochSeconds}", // Авто-название
    val isSaved: Boolean = false
)

class CreateNoteViewModel(
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateNoteState())
    val state = _state.asStateFlow()

    fun updateBiomass(value: Double) {
        _state.update { it.copy(biomassWeight = value) }
    }

    fun updateCoal(value: Double) {
        _state.update { it.copy(coalWeight = value) }
    }

    @OptIn(ExperimentalTime::class)
    fun saveNote() {
        viewModelScope.launch {
            val currentState = _state.value
            val newNote = Note(
                id = Random.nextLong().toString(), // По-хорошему здесь нужен UUID
                createdAt = kotlin.time.Clock.System.now(),
                massWeight = currentState.biomassWeight,
                massDescription = currentState.description,
                status = NoteStatus.DRAFT,
                coalWeight = currentState.coalWeight
            )

            saveNoteUseCase(newNote)

            // Сигнализируем UI, что всё готово и можно уходить назад
            _state.update { it.copy(isSaved = true) }
        }
    }
}