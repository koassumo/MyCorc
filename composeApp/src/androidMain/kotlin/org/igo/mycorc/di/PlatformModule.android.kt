package org.igo.mycorc.di

import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import com.russhwolf.settings.Settings

actual val platformModule: Module = module {
    single<Settings> {
        val sharedPrefs = androidContext().getSharedPreferences("mycorc_preferences", 0)
        SharedPreferencesSettings(sharedPrefs)
    }
}