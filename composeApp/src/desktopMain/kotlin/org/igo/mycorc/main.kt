//main.kt

package org.igo.mycorc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.igo.mycorc.di.initKoin

fun main() = application {
    // 👇 Инициализируем Koin (так же, как в iOS и Android)
    // Важно сделать это ДО запуска App()
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "MyCorc Desktop",
    ) {
        // Запускаем наше общее приложение
        App()
    }
}