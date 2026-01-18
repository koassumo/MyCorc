package org.igo.mycorc.data.remote.firestore

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonObject

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
}
