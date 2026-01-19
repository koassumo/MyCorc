package org.igo.mycorc.domain.rep_interface

import kotlinx.coroutines.flow.Flow
import org.igo.mycorc.domain.model.Note

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(noteId: String): Flow<Note?>
    suspend fun saveNote(note: Note)
}
