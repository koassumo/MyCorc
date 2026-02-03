package org.igo.mycorc.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

/**
 * Базовый текстовый компонент с дефолтными настройками.
 *
 * @param text Текст для отображения.
 * @param modifier Модификатор (padding должен быть внешним).
 * @param style Стиль текста (по умолчанию bodyLarge).
 * @param color Цвет текста (по умолчанию onSurface из темы).
 * @param textAlign Выравнивание текста.
 * @param maxLines Максимальное количество строк.
 * @param overflow Поведение при переполнении.
 */
@Composable
fun CommonText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

/**
 * Заголовок (крупный текст).
 */
@Composable
fun CommonTextTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign? = null
) {
    CommonText(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium,
        color = color,
        textAlign = textAlign
    )
}

/**
 * Обычный текст (средний размер).
 */
@Composable
fun CommonTextBody(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    CommonText(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * Мелкий текст (подписи, вспомогательный текст).
 */
@Composable
fun CommonTextSmall(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    CommonText(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}
