package org.igo.mycorc.data.mapper

import org.igo.mycorc.db.NoteEntity
import org.igo.mycorc.domain.model.Note
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class NoteDbMapper {

    @OptIn(ExperimentalTime::class)
    fun map(entity: NoteEntity): Note {
        // Мы берем данные из payload (JSON) и объединяем с метаданными из колонок SQL
        val payload = entity.payload

        return Note(
            id = entity.id,
            userId = entity.userId, // <-- Пробрасываем ID владельца
            createdAt = Instant.fromEpochMilliseconds(entity.updatedAt), // Конвертируем Long в дату
            // Берем бизнес-данные из JSON-контейнера
            massWeight = payload.biomass?.weight ?: 0.0,
            massValue = 0.0, // Этого поля нет в payload, возможно расчетное или заглушка
            massDescription = payload.locationComment ?: "Без описания",
            status = entity.status,
            coalWeight = payload.coal?.weight,

            // 👇 ВАЖНО: Мы раскомментировали эту строку.
            // Так как в SQLDelight у тебя "INTEGER AS Boolean", то entity.isSynced — это уже Boolean.
            isSynced = entity.isSynced,
            photoPath = payload.biomass?.photoPath?.takeIf { it.isNotEmpty() }, // Берем из payload
            photoUrl = payload.biomass?.photoUrl?.takeIf { it.isNotEmpty() }    // Берем из payload
        )
    }
}