package org.igo.mycorc.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.igo.mycorc.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Settings> {
        SharedPreferencesSettings(androidContext().getSharedPreferences("mycorc_preferences", 0))
    }

    // Создаем драйвер прямо тут, используя контекст
    single<SqlDriver> {
        AndroidSqliteDriver(AppDatabase.Schema, androidContext(), "mycorc.db")
    }
}