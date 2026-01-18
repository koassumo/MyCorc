package org.igo.mycorc.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseStorageResponse(
    val name: String,           // Путь к файлу в Storage (например: "users/123/packages/456/photo.jpg")
    val bucket: String,          // Имя бакета (например: "myproject.appspot.com")
    val downloadTokens: String   // Токен для публичной ссылки
)
