package org.igo.mycorc.data.remote.firestore

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class FirestorePackagesApi(
    private val client: HttpClient,
    private val projectId: String,
    private val databaseId: String = "(default)"
) {
    suspend fun upsertPackage(
        userId: String,
        noteId: String,
        documentBody: JsonObject,
        idToken: String
    ) {
        val url =
            "https://firestore.googleapis.com/v1/projects/$projectId/databases/$databaseId/documents/users/$userId/packages/$noteId"

        client.patch(url) {
            header(HttpHeaders.Authorization, "Bearer $idToken")
            contentType(ContentType.Application.Json)
            setBody(documentBody)
        }
    }

    /**
     * Получить все пакеты пользователя с сервера
     */
    suspend fun getAllPackages(
        userId: String,
        idToken: String
    ): List<Map<String, Any>> {
        val url =
            "https://firestore.googleapis.com/v1/projects/$projectId/databases/$databaseId/documents/users/$userId/packages"

        val response = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $idToken")
        }

        val responseText = response.bodyAsText()
        val json = Json { ignoreUnknownKeys = true }
        val responseJson = json.parseToJsonElement(responseText).jsonObject

        // Если документов нет, возвращаем пустой список
        val documents = responseJson["documents"]?.jsonArray ?: return emptyList()

        return documents.map { document ->
            val docObj = document.jsonObject
            val fields = docObj["fields"]?.jsonObject ?: return@map emptyMap()

            // Парсим Firestore fields в простой Map
            parseFirestoreFields(fields)
        }
    }

    /**
     * Преобразует Firestore fields формат в простой Map<String, Any>
     * Например: {"noteId": {"stringValue": "123"}} -> {"noteId": "123"}
     */
    private fun parseFirestoreFields(fields: JsonObject): Map<String, Any> {
        val result = mutableMapOf<String, Any>()

        fields.forEach { (key, value) ->
            val valueObj = value.jsonObject

            when {
                valueObj.containsKey("stringValue") -> {
                    result[key] = valueObj["stringValue"]!!.jsonPrimitive.content
                }
                valueObj.containsKey("integerValue") -> {
                    result[key] = valueObj["integerValue"]!!.jsonPrimitive.content.toLong()
                }
                valueObj.containsKey("doubleValue") -> {
                    result[key] = valueObj["doubleValue"]!!.jsonPrimitive.content.toDouble()
                }
                valueObj.containsKey("booleanValue") -> {
                    result[key] = valueObj["booleanValue"]!!.jsonPrimitive.content.toBoolean()
                }
                valueObj.containsKey("nullValue") -> {
                    // null значения пропускаем
                }
            }
        }

        return result
    }
}
