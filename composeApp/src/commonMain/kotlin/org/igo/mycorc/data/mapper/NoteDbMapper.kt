package org.igo.mycorc.data.mapper

import org.igo.mycorc.db.NoteEntity
import org.igo.mycorc.domain.model.Note
import kotlin.time.ExperimentalTime

class NoteDbMapper {

    @OptIn(ExperimentalTime::class)
    fun map(entity: NoteEntity): Note {
        // Мы берем данные из payload (JSON) и объединяем с метаданными из колонок SQL
        val payload = entity.payload

        return Note(
            id = entity.id,
            createdAt = kotlinx.datetime.Instant.fromEpochMilliseconds(entity.updatedAt), // Конвертируем Long в дату
            // Берем бизнес-данные из JSON-контейнера
            massWeight = payload.biomass?.weight ?: 0.0,
            massValue = 0.0, // Этого поля нет в payload, возможно расчетное или заглушка
            massDescription = payload.locationComment ?: "Без описания",
            status = entity.status,
            coalWeight = payload.coal?.weight,

            // 👇 Вот тут мы вручную превращаем Long (0 или 1) в Boolean
            // Если вы отключили адаптер, SQLDelight скорее всего вернул Long.
            // Если вдруг там Boolean, IDE подскажет убрать "== 1L"
            // isSynced = entity.isSynced == 1L
        )
    }
}