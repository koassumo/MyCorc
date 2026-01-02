package org.igo.mycorc

import android.app.Application
import org.igo.mycorc.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Инициализируем Koin один раз на старте всего приложения
        initKoin {
            androidLogger()
            androidContext(this@MyApp)
        }
    }
}
