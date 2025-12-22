package org.igo.mycorc.domain.model

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
data class Note(

    // --- 1. Когда начали ---
    val id: String,
    val createdAt: kotlin.time.Instant = Clock.System.now(),

    // --- 2. Что засунули (Биомасса) ---
    // Это заполняем сразу при создании
    val massWeight: Double,       // Вес сырья (кг)
    val massValue: Double,        // Объем сырья (Вес угля в кг)
    val massDescription: String,  // Описание (например "Ветки дуба" или "Опилки")

    // --- 3. Что получили (Уголь) ---
    // Здесь ставим вопросительный знак (Double?),
    // потому что в начале процесса угля еще нет (null)
    val coalWeight: Double? = null, // Вес готового угля (кг)
    val coalValue: Double? = null,
    val coalQuality: String? = null,// Качество (например "Класс А")

    // --- 4. Статус процесса ---


    val imageUrl: String? = null,// Путь к фото доказательства
    val comment: String = "",    // Заметки фермера (например, "Печь №2")
    val status: NoteStatus = NoteStatus.DRAFT    // Статус (Черновик, Отправлено)
)

// Статусы жизненного цикла записи
enum class NoteStatus {
    DRAFT,      // Лежит только на телефоне
    SYNCED,     // Улетело в Firebase
    VERIFIED,   // Проверено аудитором
    PAID        // Деньги получены (CORC выпущен)
}