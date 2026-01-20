package org.igo.mycorc.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Умный компонент для отображения загрузки поверх контента.
 *
 * Контент всегда отображается, а при загрузке поверх него накладывается
 * полупрозрачный фон с индикатором загрузки.
 *
 * @param isLoading Флаг загрузки. Если true - показывается индикатор поверх контента.
 * @param modifier Модификатор для контейнера.
 * @param content Основной контент, который всегда отображается.
 *
 * Пример использования:
 * ```
 * LoadingContent(isLoading = state.isLoading) {
 *     Scaffold(...) { ... }
 * }
 * ```
 */
@Composable
fun LoadingContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Контент всегда показываем
        content()

        // Индикатор загрузки ПОВЕРХ с полупрозрачным фоном
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
