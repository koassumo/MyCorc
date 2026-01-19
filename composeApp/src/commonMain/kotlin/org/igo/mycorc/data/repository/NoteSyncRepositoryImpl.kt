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
    override suspend fun syncNote(note: Note, markAsSent: Boolean): Result<Unit> =
        runCatching {
            println("🔄 Начало синхронизации: noteId=${note.id}, markAsSent=$markAsSent")

            val idToken = authRepository.getIdTokenOrNull()
                ?: error("Нет idToken (пользователь не залогинен или токен недоступен)")

            val user = authRepository.currentUser.firstOrNull()
                ?: error("Нет текущего пользователя (currentUser = null)")

            println("👤 User ID из authRepository: ${user.id}")
            println("👤 User ID из note: ${note.userId}")

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

            // Определяем финальный статус для отправки на сервер
            val finalStatus = if (markAsSent) NoteStatus.SENT else entity.status

            val fields = linkedMapOf(
                "noteId" to FirestoreJson.string(note.id),
                "userId" to FirestoreJson.string(user.id),
                "status" to FirestoreJson.string(finalStatus.name),

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

            if (markAsSent) {
                // Проверяем запись ДО обновления
                val beforeUpdate = db.noteQueries.getNoteById(note.id).executeAsOneOrNull()
                println("🔍 ДО обновления: status=${beforeUpdate?.status}, isSynced=${beforeUpdate?.isSynced}, userId=${beforeUpdate?.userId}")

                // Финальная отправка - меняем статус на SENT
                println("📝 Вызов markNoteSynced: noteId=${note.id}, userId=${user.id}, status=SENT")
                db.noteQueries.markNoteSynced(
                    status = NoteStatus.SENT,
                    updatedAt = nowMillis,
                    id = note.id,
                    userId = user.id
                )
                println("✅ SQL-запрос markNoteSynced выполнен")

                // Проверяем обновление ПОСЛЕ
                val afterUpdate = db.noteQueries.getNoteById(note.id).executeAsOneOrNull()
                println("🔍 ПОСЛЕ обновления: status=${afterUpdate?.status}, isSynced=${afterUpdate?.isSynced}, userId=${afterUpdate?.userId}")
            } else {
                // Автосохранение черновика - только помечаем как синхронизированное
                db.noteQueries.markNoteAsSynced(
                    updatedAt = nowMillis,
                    id = note.id,
                    userId = user.id
                )
                println("✅ Черновик синхронизирован: статус не изменён, isSynced = true")
            }
            println("✅ Синхронизация завершена успешно")
        }

    override suspend fun syncFromServer(): Result<Unit> =
        runCatching {
            println("🔄 Начало синхронизации с сервера")

            val idToken = authRepository.getIdTokenOrNull()
                ?: error("Нет idToken (пользователь не залогинен)")

            val user = authRepository.currentUser.firstOrNull()
                ?: error("Нет текущего пользователя")

            println("👤 Синхронизация для пользователя: ${user.id}")

            // 1. Загрузить все пакеты с сервера
            val serverPackages = firestoreApi.getAllPackages(user.id, idToken)
            println("📥 Получено ${serverPackages.size} пакетов с сервера")

            // 2. Загрузить все локальные записи
            val localNotes = db.noteQueries.getAllNotes(user.id).executeAsList()
            println("💾 Локально: ${localNotes.size} записей")

            // 3. УДАЛЕНИЕ: Локальные записи, которых нет на сервере
            val serverIds = serverPackages.map { it["noteId"] as String }.toSet()
            localNotes.filter { it.id !in serverIds }.forEach { localNote ->
                if (!localNote.isSynced) {
                    println("⚠️ Пропускаем удаление ${localNote.id}: есть несинхронизированные изменения")
                } else {
                    println("🗑️ Удаляем локальную запись: ${localNote.id}")
                    // Удаляем фото (если есть в biomass)
                    val entity = db.noteQueries.getNoteById(localNote.id).executeAsOneOrNull()
                    entity?.payload?.biomass?.photoPath?.let { photoPath ->
                        if (photoPath.isNotEmpty()) {
                            imageStorage.deleteImage(photoPath)
                        }
                    }
                    // Удаляем из БД
                    db.noteQueries.deleteNote(localNote.id, user.id)
                }
            }

            // 4. ОБНОВЛЕНИЕ/СОЗДАНИЕ из серверных данных
            serverPackages.forEach { serverPackage ->
                val noteId = serverPackage["noteId"] as String
                val localNote = localNotes.find { it.id == noteId }

                if (localNote == null) {
                    println("➕ Создаем новый пакет: $noteId")
                    createNoteFromServer(serverPackage, user.id)
                } else if (shouldUpdateFromServer(serverPackage, localNote)) {
                    println("🔄 Обновляем пакет: $noteId")
                    updateNoteFromServer(serverPackage, user.id)
                } else {
                    println("✓ Пакет актуален: $noteId")
                }
            }

            println("✅ Синхронизация с сервера завершена")
        }

    /**
     * Проверяет, нужно ли обновить локальную запись данными с сервера
     */
    private fun shouldUpdateFromServer(
        serverPackage: Map<String, Any>,
        localNote: org.igo.mycorc.db.NoteEntity
    ): Boolean {
        val serverStatus = NoteStatus.valueOf(serverPackage["status"] as String)
        val localStatus = localNote.status

        // ПРАВИЛО 1: Если на сервере "отправлено на регистрацию" - это истина
        val lockedStatuses = setOf(NoteStatus.SENT, NoteStatus.APPROVED, NoteStatus.REJECTED)

        if (serverStatus in lockedStatuses) {
            println("  ↳ Сервер заблокирован ($serverStatus) - обновляем локально")
            return true
        }

        // ПРАВИЛО 2: Сравниваем по времени обновления
        val serverUpdatedAt = (serverPackage["updatedAtEpochMillis"] as? Long) ?: 0L
        val localUpdatedAt = localNote.updatedAt

        if (serverUpdatedAt > localUpdatedAt) {
            println("  ↳ Сервер новее: $serverUpdatedAt > $localUpdatedAt")
            return true
        }

        println("  ↳ Локально актуально: $localUpdatedAt >= $serverUpdatedAt")
        return false
    }

    /**
     * Создает новую запись из серверных данных
     */
    private fun createNoteFromServer(serverPackage: Map<String, Any>, userId: String) {
        val noteId = serverPackage["noteId"] as String
        val status = NoteStatus.valueOf(serverPackage["status"] as String)
        val updatedAt = (serverPackage["updatedAtEpochMillis"] as? Long) ?: 0L

        // Парсим payloadJson обратно в NotePayload
        val payloadJson = (serverPackage["payloadJson"] as? String) ?: "{}"
        val payload = json.decodeFromString<org.igo.mycorc.domain.model.NotePayload>(payloadJson)

        db.noteQueries.insertNoteFromServer(
            id = noteId,
            userId = userId,
            status = status,
            updatedAt = updatedAt,
            payload = payload
        )
    }

    /**
     * Обновляет существующую запись данными с сервера
     */
    private fun updateNoteFromServer(serverPackage: Map<String, Any>, userId: String) {
        val noteId = serverPackage["noteId"] as String
        val status = NoteStatus.valueOf(serverPackage["status"] as String)
        val updatedAt = (serverPackage["updatedAtEpochMillis"] as? Long) ?: 0L

        // Парсим payloadJson обратно в NotePayload
        val payloadJson = (serverPackage["payloadJson"] as? String) ?: "{}"
        val payload = json.decodeFromString<org.igo.mycorc.domain.model.NotePayload>(payloadJson)

        db.noteQueries.updateNoteFromServer(
            status = status,
            updatedAt = updatedAt,
            payload = payload,
            id = noteId,
            userId = userId
        )
    }
}
