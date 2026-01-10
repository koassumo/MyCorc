package org.igo.mycorc.di

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import kotlinx.serialization.json.Json
import org.igo.mycorc.db.AppDatabase
import org.igo.mycorc.db.NoteEntity
import org.igo.mycorc.domain.model.NotePayload
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.rep_interface.SettingsRepository
import org.igo.mycorc.data.repository.SettingsRepositoryImpl
import org.koin.dsl.module

val dataModule = module {

    // 1. Сама База Данных (Singleton)
    single<AppDatabase> {
        // ИЗМЕНЕНИЕ: Просим сразу готовый драйвер (он создается в PlatformModule)
        val driver = get<SqlDriver>()

        // --- МАГИЯ JSON АДАПТЕРА ---
        val payloadAdapter = object : ColumnAdapter<NotePayload, String> {
            override fun decode(databaseValue: String) = Json.decodeFromString<NotePayload>(databaseValue)
            override fun encode(value: NotePayload) = Json.encodeToString(value)
        }

        val statusAdapter = object : ColumnAdapter<NoteStatus, String> {
            override fun decode(databaseValue: String) = NoteStatus.valueOf(databaseValue)
            override fun encode(value: NoteStatus) = value.name
        }

        val booleanAdapter = object : ColumnAdapter<Boolean, Long> {
            override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
            override fun encode(value: Boolean): Long = if (value) 1L else 0L
        }

        // Создаем и возвращаем готовую базу
        AppDatabase(
            driver = driver,
            noteEntityAdapter = NoteEntity.Adapter(
                statusAdapter = statusAdapter,
                isSyncedAdapter = booleanAdapter,
                payloadAdapter = payloadAdapter
            )
        )
    }

    // Репозиторий настроек
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    // Репозиторий заметок (пока закомментирован, включим на следующем шаге)
    // single<NoteRepository> { NoteRepositoryImpl(get()) }
}