package org.igo.mycorc.domain.rep_interface

import org.igo.mycorc.domain.model.Note

interface NoteSyncRepository {
    suspend fun syncNote(note: Note, markAsSent: Boolean = true): Result<Unit>
    suspend fun syncFromServer(): Result<Unit>
}
