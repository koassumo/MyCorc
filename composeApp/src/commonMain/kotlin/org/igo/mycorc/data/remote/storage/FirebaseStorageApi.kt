package org.igo.mycorc.data.remote.storage

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.igo.mycorc.data.remote.dto.FirebaseStorageResponse

class FirebaseStorageApi(
    private val client: HttpClient,
    private val bucketName: String
) {
    private val baseUrl = "https://firebasestorage.googleapis.com/v0/b/$bucketName/o"

    /**
     * Загружает фото в Firebase Storage и возвращает пару (storagePath, downloadUrl)
     *
     * @param userId ID пользователя
     * @param noteId ID заметки
     * @param photoBytes Байты изображения
     * @param idToken Firebase ID token для авторизации
     * @return Pair(storagePath, downloadUrl) - путь в Storage и публичная ссылка
     */
    suspend fun uploadPhoto(
        userId: String,
        noteId: String,
        photoBytes: ByteArray,
        idToken: String
    ): Pair<String, String> {
        // Формируем путь: users/{userId}/packages/{noteId}/photo.jpg
        val storagePath = "users/$userId/packages/$noteId/photo.jpg"

        // Загружаем файл
        val response: FirebaseStorageResponse = client.post(baseUrl) {
            url {
                parameters.append("name", storagePath)
            }
            header(HttpHeaders.Authorization, "Bearer $idToken")
            contentType(ContentType.Image.JPEG)
            setBody(photoBytes)
        }.body()

        // Склеиваем публичную ссылку
        // Кодируем путь: заменяем "/" на "%2F" для URL
        val encodedPath = response.name.replace("/", "%2F")
        val downloadUrl = "https://firebasestorage.googleapis.com/v0/b/${response.bucket}/o/$encodedPath?alt=media&token=${response.downloadTokens}"

        return Pair(storagePath, downloadUrl)
    }
}
