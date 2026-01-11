package org.igo.mycorc.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.igo.mycorc.db.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import com.russhwolf.settings.Settings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences
import org.igo.mycorc.data.local.JvmImageStorage
import org.igo.mycorc.data.local.ImageStorage

actual val platformModule: Module = module {
    single<SqlDriver> {
        val dbFile = File("mycorc.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        if (!dbFile.exists()) {
            AppDatabase.Schema.create(driver)
        }
        driver
    }

    single<Settings> {
        val preferences = Preferences.userRoot().node("org.igo.mycorc")
        PreferencesSettings(preferences)
    }

    single<ImageStorage> {
        JvmImageStorage()
    }
}
