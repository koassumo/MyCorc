package org.igo.mycorc.data.repository

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.igo.mycorc.data.remote.firestore.FirestoreJson
import org.igo.mycorc.data.remote.firestore.FirestorePackagesApi
import org.igo.mycorc.db.AppDatabase
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.rep_interface.AuthRepository
import org.igo.mycorc.domain.rep_interface.NoteSyncRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class NoteSyncRepositoryImpl(
    private val db: AppDatabase,
    private val authRepository: AuthRepository,
    private val api: FirestorePackagesApi
) : NoteSyncRepository {

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    @OptIn(ExperimentalTime::class)
    override suspend fun syncNote(note: Note): Result<Unit> =
        runCatching {
            val idToken = authRepository.getIdTokenOrNull()
                ?: error("Нет idToken (пользователь не залогинен или токен недоступен)")

            val user = authRepository.currentUser.firstOrNull()
                ?: error("Нет текущего пользователя (currentUser = null)")

            val entity = db.noteQueries.getNoteById(note.id).executeAsOneOrNull()
                ?: error("Запись не найдена в локальной БД: noteId=${note.id}")

            val payloadJson = json.encodeToString(entity.payload)

            val fields = linkedMapOf(
                "noteId" to FirestoreJson.string(note.id),
                "userId" to FirestoreJson.string(user.id),
                "status" to FirestoreJson.string(entity.status.name),

                "createdAtEpochMillis" to FirestoreJson.integer(note.createdAt.toEpochMilliseconds()),
                "updatedAtEpochMillis" to FirestoreJson.integer(entity.updatedAt),

                "massWeight" to FirestoreJson.double(note.massWeight),
                "massDescription" to FirestoreJson.string(note.massDescription),
                "massValue" to FirestoreJson.double(note.massValue),

                "coalWeight" to (note.coalWeight?.let { FirestoreJson.double(it) } ?: FirestoreJson.nullValue()),
                "photoPath" to (note.photoPath?.let { FirestoreJson.string(it) } ?: FirestoreJson.nullValue()),

                // payload сохраняем строкой, чтобы не строить глубокий mapValue
                "payloadJson" to FirestoreJson.string(payloadJson)
            )

            api.upsertPackage(
                noteId = note.id,
                documentBody = FirestoreJson.document(fields),
                idToken = idToken
            )

            val nowMillis = Clock.System.now().toEpochMilliseconds()

            db.noteQueries.markNoteSynced(
                status = NoteStatus.SENT,
                updatedAt = nowMillis,
                id = note.id,
                userId = user.id
            )
        }
}
