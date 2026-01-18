package org.igo.mycorc.domain.model

import kotlin.time.Instant
import kotlin.time.ExperimentalTime


// Это основной класс, с которым работает UI.
// Мы добавили сюда поля, которые раньше были спрятаны в JSON, чтобы UI мог их показать.
data class Note @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val userId: String = "",
    val createdAt: Instant,
    val status: NoteStatus,

    // Данные о биомассе
    val massWeight: Double = 0.0,
    val massDescription: String = "",
    val massValue: Double = 0.0,

    // Данные об угле (может быть null)
    val coalWeight: Double? = null,

    // Фото
    val photoPath: String? = null,    // Локальный путь на устройстве или путь в Storage ("users/123/packages/456/photo.jpg")
    val photoUrl: String? = null,     // Публичная ссылка с сервера (для UI)

    // Технические поля
    val isSynced: Boolean = false
)