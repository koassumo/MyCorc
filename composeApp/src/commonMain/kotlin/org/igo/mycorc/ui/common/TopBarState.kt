package org.igo.mycorc.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Реактивное состояние верхнего бара.
 * Каждый дочерний экран заполняет эти поля — MainScreen отображает.
 */
class TopBarState {
    var title by mutableStateOf("")
    var canNavigateBack by mutableStateOf(false)
    var onNavigateBack: () -> Unit = {}
}

//доставка состояния верхнего бара
val LocalTopBarState = staticCompositionLocalOf<TopBarState> {
    error("TopBarState not provided")
}
