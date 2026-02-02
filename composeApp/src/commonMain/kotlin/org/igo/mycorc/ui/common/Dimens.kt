package org.igo.mycorc.ui.common

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Централизованные размеры и отступы для UI компонентов.
 * Сгруппированы по типам компонентов для удобства навигации.
 */
object Dimens {

    // ==================== Screen / Display ====================
    val ScreenPaddingSides = 16.dp
    val ScreenPaddingTop = 16.dp
    val ScreenPaddingBottom = 16.dp

    // ==================== CommonCard ====================
    val CommonCardPaddingTop = 8.dp
    val CommonCardPaddingBottom = 0.dp
    val CommonCardContentPaddingTop = 24.dp
    val CommonCardContentPaddingSides = 20.dp
    val CommonCardContentPaddingBottom = 24.dp
    val CommonCardCornerRadius = 28.dp
    val CommonCardElevation = 0.dp  // Тени не используем (Material Design 3 + iOS compatibility)
    val CommonCardBorderWidth = 0.dp
    val CommonCardAlpha = 1f

    // ==================== Card (общие карточки) ====================
    val CardCornerRadius = 16.dp
    val CardElevation = 0.dp  // Тени не используем (Material Design 3 + iOS compatibility)
    val CardPadding = 16.dp
    val CardItemSpacing = 12.dp

    // ==================== CommonButton ====================
    val CommonButtonHeight = 50.dp
    val CommonButtonCornerRadius = 24.dp

    // Обратная совместимость (deprecated, используй CommonButtonHeight)
    val ButtonHeight = CommonButtonHeight

    // ==================== Spacing (универсальные отступы) ====================
    val SpaceSmall = 8.dp
    val SpaceMedium = 16.dp
    val SpaceLarge = 24.dp
    val SpaceExtraLarge = 32.dp

    // ==================== Components ====================
    val ChipHeight = 40.dp

    // ==================== Text Sizes ====================
    val TextSizeTitle = 20.sp
    val TextSizeBody = 16.sp
}
