package org.igo.mycorc.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.igo.mycorc.db.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import java.util.prefs.Preferences

actual val platformModule: Module = module {
    single<Settings> {
        PreferencesSettings(Preferences.userRoot())
    }

    single<SqlDriver> {
        val dbPath = File(System.getProperty("user.home"), "mycorc.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbPath.absolutePath}")

        // На десктопе нужно создавать таблицы вручную, если файла нет
        if (!dbPath.exists()) {
            AppDatabase.Schema.create(driver)
        }
        driver
    }
}