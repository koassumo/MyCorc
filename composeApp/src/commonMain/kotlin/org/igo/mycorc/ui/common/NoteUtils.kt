package org.igo.mycorc.ui.common

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.igo.mycorc.domain.model.Note

/**
 * Форматирует заголовок заметки на основе даты создания.
 * Формат: YYYYMMDD-HH:MM:SS
 * Пример: 20260122-14:35:45
 */
@kotlin.time.ExperimentalTime
fun formatNoteTitle(note: Note): String {
    val localDateTime = note.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
    val year = localDateTime.year
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    val second = localDateTime.second.toString().padStart(2, '0')
    return "$year$month$day-$hour:$minute:$second"
}
