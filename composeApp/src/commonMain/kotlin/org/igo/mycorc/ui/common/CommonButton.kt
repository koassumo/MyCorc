package org.igo.mycorc.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
 * @param leadingIcon Опциональная иконка слева от текста.
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
    leadingIcon: (@Composable () -> Unit)? = null,
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(Dimens.SpaceSmall))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

/**
 * Унифицированная кнопка с обводкой (outlined) и дефолтными стилями.
 *
 * @param text Текст кнопки.
 * @param onClick Callback при нажатии.
 * @param modifier Модификатор (padding должен быть внешним).
 * @param enabled Активна ли кнопка.
 * @param leadingIcon Опциональная иконка слева от текста.
 * @param contentColor Цвет текста и обводки.
 * @param height Высота кнопки.
 * @param cornerRadius Радиус скругления углов.
 */
@Composable
fun CommonOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = Dimens.CommonButtonHeight,
    cornerRadius: Dp = Dimens.CommonButtonCornerRadius
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor
        ),
        border = BorderStroke(Dimens.BorderWidthStandard, contentColor),
        contentPadding = PaddingValues(horizontal = Dimens.SpaceMedium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(Dimens.SpaceSmall))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
