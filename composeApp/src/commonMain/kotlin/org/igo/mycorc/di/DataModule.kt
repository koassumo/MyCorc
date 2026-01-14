//DataModule.kt

package org.igo.mycorc.di

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.igo.mycorc.db.AppDatabase
import org.igo.mycorc.db.NoteEntity
import org.igo.mycorc.domain.model.NotePayload
import org.igo.mycorc.domain.model.NoteStatus
import org.igo.mycorc.domain.rep_interface.SettingsRepository
import org.igo.mycorc.data.repository.SettingsRepositoryImpl
import org.igo.mycorc.data.mapper.NoteDbMapper
import org.igo.mycorc.domain.rep_interface.NoteRepository
import org.igo.mycorc.data.repository.NoteRepositoryImpl
import org.koin.dsl.module
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.igo.mycorc.domain.rep_interface.AuthRepository
import org.igo.mycorc.data.repository.AuthRepositoryImpl

val dataModule = module {

    // так нельзя - на десктопе Firebase не работает
    //single { Firebase.auth }
    //single<AuthRepository> { AuthRepositoryImpl(get()) }

    // 1. Сама База Данных
    single<AppDatabase> {
        val driver = get<SqlDriver>()

        val payloadAdapter = object : ColumnAdapter<NotePayload, String> {
            override fun decode(databaseValue: String) = Json.decodeFromString<NotePayload>(databaseValue)
            override fun encode(value: NotePayload) = Json.encodeToString(value)
        }

        val statusAdapter = object : ColumnAdapter<NoteStatus, String> {
            override fun decode(databaseValue: String) = NoteStatus.valueOf(databaseValue)
            override fun encode(value: NoteStatus) = value.name
        }

        // Адаптер для boolean пока оставляем, может пригодиться, но в конструктор не передаем,
        // раз SQLDelight сгенерировал Long.
        val booleanAdapter = object : ColumnAdapter<Boolean, Long> {
            override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
            override fun encode(value: Boolean): Long = if (value) 1L else 0L
        }

        AppDatabase(
            driver = driver,
            noteEntityAdapter = NoteEntity.Adapter(
                statusAdapter = statusAdapter,
                // isSyncedAdapter = booleanAdapter, // Закомментировано, так как SQLDelight ждет Long
                payloadAdapter = payloadAdapter
            )
        )
    }

    // 2. Репозиторий настроек
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    // 3. 👇 НОВАЯ ЧАСТЬ: Подключаем работу с заметками

    // Сначала учим Koin создавать Маппер (он нужен Репозиторию)
    factory { NoteDbMapper() }

    // Теперь создаем Репозиторий.
    // get() -> AppDatabase
    // get() -> NoteDbMapper
    // get() -> AuthRepository (добавили третьим параметром для фильтрации по юзеру)
    single<NoteRepository> { NoteRepositoryImpl(get(), get(), get()) }
}