//KoinStarter.kt

package org.igo.mycorc.di

import org.koin.core.context.startKoin

fun startKoinIos() {
    // Вызываем нашу общую функцию initKoin из commonMain
    // Здесь можно передать ios-специфичные настройки, если понадобятся
    initKoin()
}