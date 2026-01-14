// PlatformModule.ios.kt
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
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.igo.mycorc.domain.rep_interface.AuthRepository
import org.igo.mycorc.data.repository.AuthRepositoryImpl

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

    single { Firebase.auth }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

}
