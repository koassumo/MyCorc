package org.igo.mycorc.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

/**
 * Унифицированная кнопка с дефолтными стилями.
 *
 * @param text Текст кнопки.
 * @param onClick Callback при нажатии.
 * @param modifier Модификатор (padding должен быть внешним).
 * @param enabled Активна ли кнопка.
 * @param containerColor Цвет фона кнопки.
 * @param contentColor Цвет текста.
 * @param height Высота кнопки.
 * @param cornerRadius Радиус скругления углов.
 */
@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    height: Dp = Dimens.CommonButtonHeight,
    cornerRadius: Dp = Dimens.CommonButtonCornerRadius
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = Dimens.SpaceMedium)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
