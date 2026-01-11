package org.igo.mycorc.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.igo.mycorc.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.igo.mycorc.data.local.AndroidImageStorage
import org.igo.mycorc.data.local.ImageStorage

actual val platformModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = androidContext(),
            name = "mycorc.db"
        )
    }

    single<Settings> {
        val sharedPrefs = androidContext().getSharedPreferences("mycorc_settings", 0)
        SharedPreferencesSettings(sharedPrefs)
    }

    single<ImageStorage> {
        AndroidImageStorage(androidContext())
    }
}
