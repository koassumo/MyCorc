package org.igo.mycorc.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.igo.mycorc.db.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import com.russhwolf.settings.Settings
import com.russhwolf.settings.NSUserDefaultsSettings
import platform.Foundation.NSUserDefaults
import org.igo.mycorc.data.local.IosImageStorage
import org.igo.mycorc.data.local.ImageStorage

actual val platformModule: Module = module {

    single<SqlDriver> {
        NativeSqliteDriver(AppDatabase.Schema, "mycorc.db")
    }

    single<Settings> {
        val userDefaults = NSUserDefaults.standardUserDefaults
        NSUserDefaultsSettings(userDefaults)
    }

    single<ImageStorage> {
        IosImageStorage()
    }
}
