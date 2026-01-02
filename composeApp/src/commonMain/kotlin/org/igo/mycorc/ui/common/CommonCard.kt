package org.igo.mycorc.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CommonCard(
    modifier: Modifier = Modifier,

    // Внешние отступы (Margin)
    cardPadding: PaddingValues = PaddingValues(
        top = Dimens.CommonCardPaddingTop,
        start = Dimens.ScreenPaddingSides,
        end = Dimens.ScreenPaddingSides,
        bottom = Dimens.CommonCardPaddingBottom),

    // Внутренние отступы (Padding) - берем из Dimens
    contentPadding: PaddingValues = PaddingValues(
        top = Dimens.CommonCardContentPaddingTop,
        //start = Dimens.CommonCardContentPaddingSides,
        //end = Dimens.CommonCardContentPaddingSides,
        bottom = Dimens.CommonCardContentPaddingBottom
    ),

    cornerRadius: Dp = Dimens.CommonCardCornerRadius,
    elevation: Dp = Dimens.CommonCardElevation,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    borderColor: Color? = null,
    borderWidth: Dp = Dimens.CommonCardBorderWidth,

    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(cardPadding)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = if (borderColor != null) BorderStroke(borderWidth, borderColor) else null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(contentPadding),
            horizontalAlignment = Alignment.Start,
            content = content
        )
    }
}