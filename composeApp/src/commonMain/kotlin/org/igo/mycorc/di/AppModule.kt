package org.igo.mycorc.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// Этот метод мы будем вызывать из Android и iOS
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this) // Тут применятся платформенные настройки (Android Logger, Context)
        modules(
            appModule,
            dataModule,
            domainModule,
            uiModule
        )
    }
}

val appModule = module {
    // Сюда можно класть общие утилиты, как Globals во Flux
    // singleOf(::Globals)
}
