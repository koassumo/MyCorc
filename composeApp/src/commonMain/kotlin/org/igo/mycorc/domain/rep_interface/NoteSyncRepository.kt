package org.igo.mycorc.domain.rep_interface

import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NoteStatus

interface NoteSyncRepository {
    suspend fun syncNote(note: Note, markAsSent: Boolean = true): Result<Unit>
    suspend fun syncFromServer(): Result<Unit>

    /**
     * Синхронизирует один конкретный пакет с сервера.
     * Используется после обнаружения блокировки для быстрого обновления UI.
     */
    suspend fun syncSingleNoteFromServer(noteId: String): Result<Unit>

    /**
     * Проверяет статус пакета на сервере.
     * Возвращает null, если пакета нет на сервере.
     * Используется для проверки, не заблокирован ли пакет (SENT/APPROVED/REJECTED)
     */
    suspend fun checkServerStatus(noteId: String): Result<NoteStatus?>
}
