package org.igo.mycorc.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.igo.mycorc.data.mapper.NoteDbMapper
import org.igo.mycorc.db.AppDatabase
import org.igo.mycorc.domain.model.Note
import org.igo.mycorc.domain.model.NotePayload
import org.igo.mycorc.domain.rep_interface.AuthRepository
import org.igo.mycorc.domain.rep_interface.NoteRepository
import org.igo.mycorc.core.time.TimeProvider
import kotlin.time.ExperimentalTime

class NoteRepositoryImpl(
    private val db: AppDatabase,
    private val mapper: NoteDbMapper, // 👇 Нам понадобится маппер
    private val authRepository: AuthRepository,
    private val timeProvider: TimeProvider
) : NoteRepository {

    private val queries = db.noteQueries

    // Пока в интерфейсе NoteRepository (файл NoteRepository.kt) может быть пусто,
    // но согласно ТЗ методы должны быть примерно такие:

    // 1. Получить список (Flow, чтобы UI обновлялся сам)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllNotes(): Flow<List<Note>> {
        // Мы подписываемся на User Flow.
        // Если юзер меняется (зашел/вышел), этот поток переключит запрос к БД.
        return authRepository.currentUser.flatMapLatest { user ->
            if (user == null) {
                // Если никто не залогинен — отдаем пустой список
                flowOf(emptyList())
            } else {
                // Если залогинен — слушаем таблицу, фильтруя по user.id
                queries.getAllNotes(userId = user.id)
                    .asFlow()
                    .mapToList(Dispatchers.Default) // Слушаем изменения таблицы
                    .map { entities ->
                        entities.map { mapper.map(it) }  // Превращаем каждую строку БД в Note
                    }
            }
        }
    }

    // 1.1 Получить заметку по ID (Flow, чтобы UI обновлялся автоматически)
    override fun getNoteById(noteId: String): Flow<Note?> {
        return queries.getNoteById(noteId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity ->
                entity?.let { mapper.map(it) }
            }
    }

    // 2. Создать или обновить заметку
    @OptIn(ExperimentalTime::class)
    override suspend fun saveNote(note: Note) {

        // Нам нужно собрать NotePayload обратно из полей Note
        // Это упрощенная логика, т.к. у вас Note и Payload немного разъехались в структурах

        // Получаем текущего юзера один раз (без подписки)

        val currentUser = authRepository.currentUser.firstOrNull()
            ?: throw IllegalStateException("Попытка сохранить заметку без авторизации")
        val payload = NotePayload(
            step = "BIOMASS",
            locationComment = note.massDescription,
            biomass = org.igo.mycorc.domain.model.BiomassData(
                weight = note.massWeight,
                photoPath = note.photoPath ?: "",  // 👈 Сохраняем путь к фото
                photoUrl = note.photoUrl ?: ""     // 👈 Сохраняем URL (пока пустой)
            ),
            coal = note.coalWeight?.let {
                org.igo.mycorc.domain.model.CoalData(weight = it)
            }
        )

        queries.insertNote(
            id = note.id,
            userId = currentUser.id, // <-- БЕРЕМ РЕАЛЬНЫЙ ID ЮЗЕРА
            status = note.status,
            updatedAt = timeProvider.nowEpochMillis(), // <-- ИСПОЛЬЗУЕМ ТЕКУЩЕЕ ВРЕМЯ

            // 👇 ИСПРАВЛЕНИЕ: Берем значение из самой заметки!
            // Если мы нажали "Отправить", тут прилетит true.
            isSynced = note.isSynced,
            payload = payload
        )
    }
}
