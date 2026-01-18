package org.igo.mycorc.data.repository

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.igo.mycorc.data.local.ImageStorage
import org.igo.mycorc.data.remote.firestore.FirestoreJson
import org.igo.mycorc.data.remote.firestore.FirestorePackagesApi
import org.igo.mycorc.data.remote.storage.FirebaseStorageApi
import org.igo.mycorc.db.AppDatabase
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.rep_interface.AuthRepository
import org.igo.mycorc.domain.rep_interface.NoteSyncRepository
import org.igo.mycorc.core.time.TimeProvider
import kotlin.time.ExperimentalTime

class NoteSyncRepositoryImpl(
    private val db: AppDatabase,
    private val authRepository: AuthRepository,
    private val firestoreApi: FirestorePackagesApi,
    private val storageApi: FirebaseStorageApi,
    private val imageStorage: ImageStorage,
    private val timeProvider: TimeProvider
) : NoteSyncRepository {

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    @OptIn(ExperimentalTime::class)
    override suspend fun syncNote(note: Note): Result<Unit> =
        runCatching {
            println("🔄 Начало синхронизации: noteId=${note.id}")

            val idToken = authRepository.getIdTokenOrNull()
                ?: error("Нет idToken (пользователь не залогинен или токен недоступен)")

            val user = authRepository.currentUser.firstOrNull()
                ?: error("Нет текущего пользователя (currentUser = null)")

            println("👤 User ID: ${user.id}")

            val entity = db.noteQueries.getNoteById(note.id).executeAsOneOrNull()
                ?: error("Запись не найдена в локальной БД: noteId=${note.id}")

            val payloadJson = json.encodeToString(entity.payload)

            // Загружаем фото в Storage (если есть)
            var photoStoragePath: String? = null
            var photoDownloadUrl: String? = null

            if (note.photoPath != null) {
                println("📸 Найдено фото: ${note.photoPath}")
                val photoBytes = imageStorage.loadImage(note.photoPath)
                if (photoBytes != null) {
                    println("📤 Загружаем фото в Storage (${photoBytes.size} bytes)...")
                    val (storagePath, downloadUrl) = storageApi.uploadPhoto(
                        userId = user.id,
                        noteId = note.id,
                        photoBytes = photoBytes,
                        idToken = idToken
                    )
                    photoStoragePath = storagePath
                    photoDownloadUrl = downloadUrl
                    println("✅ Фото загружено: $downloadUrl")
                } else {
                    println("⚠️ Не удалось загрузить файл: ${note.photoPath}")
                }
            } else {
                println("ℹ️ Фото отсутствует")
            }

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
                "photoPath" to (photoStoragePath?.let { FirestoreJson.string(it) } ?: FirestoreJson.nullValue()),
                "photoUrl" to (photoDownloadUrl?.let { FirestoreJson.string(it) } ?: FirestoreJson.nullValue()),

                // payload сохраняем строкой, чтобы не строить глубокий mapValue
                "payloadJson" to FirestoreJson.string(payloadJson)
            )

            println("📤 Отправляем в Firestore...")
            firestoreApi.upsertPackage(
                userId = user.id,
                noteId = note.id,
                documentBody = FirestoreJson.document(fields),
                idToken = idToken
            )
            println("✅ Данные отправлены в Firestore")

            val nowMillis = timeProvider.nowEpochMillis()

            db.noteQueries.markNoteSynced(
                status = NoteStatus.SENT,
                updatedAt = nowMillis,
                id = note.id,
                userId = user.id
            )
            println("✅ Синхронизация завершена успешно")
        }
}
