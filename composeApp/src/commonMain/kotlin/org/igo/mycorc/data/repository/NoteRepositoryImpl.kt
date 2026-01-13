package org.igo.mycorc.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.igo.mycorc.data.mapper.NoteDbMapper
import org.igo.mycorc.db.AppDatabase
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NotePayload
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.rep_interface.NoteRepository
import kotlin.time.ExperimentalTime

class NoteRepositoryImpl(
    private val db: AppDatabase,
    private val mapper: NoteDbMapper // 👇 Нам понадобится маппер
) : NoteRepository {

    private val queries = db.noteQueries

    // Пока в интерфейсе NoteRepository (файл NoteRepository.kt) может быть пусто,
    // но согласно ТЗ методы должны быть примерно такие:

    // 1. Получить список (Flow, чтобы UI обновлялся сам)
    override fun getAllNotes(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO) // Слушаем изменения таблицы
            .map { entities ->
                entities.map { mapper.map(it) } // Превращаем каждую строку БД в Note
            }
    }

    // 2. Создать или обновить заметку
    @OptIn(ExperimentalTime::class)
    override suspend fun saveNote(note: Note) {
        // Нам нужно собрать NotePayload обратно из полей Note
        // Это упрощенная логика, т.к. у вас Note и Payload немного разъехались в структурах
        val payload = NotePayload(
            step = "BIOMASS", // Пример
            locationComment = note.massDescription,
            // ... заполнить остальные поля biomass/coal из note
        )

        queries.insertNote(
            id = note.id,
            userId = "user_1", // Пока хардкод, позже возьмем из настроек
            status = note.status,
            updatedAt = note.createdAt.toEpochMilliseconds(),

            // 👇 ИСПРАВЛЕНИЕ: Берем значение из самой заметки!
            // Если мы нажали "Отправить", тут прилетит true.
            isSynced = note.isSynced,
            payload = payload
        )
    }
}
