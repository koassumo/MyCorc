package org.igo.mycorc.data.remote.firestore

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object FirestoreJson {

    fun document(fields: Map<String, JsonElement>): JsonObject =
        buildJsonObject { put("fields", JsonObject(fields)) }

    fun string(value: String): JsonObject =
        buildJsonObject { put("stringValue", value) }

    fun bool(value: Boolean): JsonObject =
        buildJsonObject { put("booleanValue", value) }

    fun double(value: Double): JsonObject =
        buildJsonObject { put("doubleValue", value) }

    // Firestore integerValue в REST ожидает строку
    fun integer(value: Long): JsonObject =
        buildJsonObject { put("integerValue", value.toString()) }

    fun nullValue(): JsonObject =
        buildJsonObject { put("nullValue", JsonPrimitive("NULL_VALUE")) }
}
