package org.igo.mycorc.domain.model

import kotlin.time.Instant
import kotlin.time.ExperimentalTime

data class Note @OptIn(ExperimentalTime::class) constructor(
    val id: String,

    // --- 1. Когда начали ---
    // Было: val createdAt: kotlin.time.Instant (Ошибка)
    // Стало: Instant (из kotlinx.datetime)
    val createdAt: Instant = kotlin.time.Clock.System.now(),


    // --- 2. Что засунули (Биомасса) ---
    val massWeight: Double,       // Вес сырья (кг)
    val massValue: Double,        // Объем сырья
    val massDescription: String,  // Описание

    // --- 3. Что получили (Уголь) ---
    val coalWeight: Double? = null,
    val coalValue: Double? = null,
    val coalQuality: String? = null,

    // --- 4. Прочее ---
    val imageUrl: String? = null,
    val comment: String = "",
    val status: NoteStatus = NoteStatus.DRAFT
)



// Статусы жизненного цикла записи
enum class NoteStatus {
    DRAFT,      // Лежит только на телефоне
    SYNCED,     // Улетело в Firebase
    VERIFIED,   // Проверено аудитором
    PAID        // Деньги получены (CORC выпущен)
}