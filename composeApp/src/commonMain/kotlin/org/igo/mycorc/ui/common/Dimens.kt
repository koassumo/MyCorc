package org.igo.mycorc.ui.common

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Централизованные размеры и отступы для UI компонентов.
 * Сгруппированы по типам компонентов для удобства навигации.
 */
object Dimens {

    // ==================== Screen / Display ====================
    val ScreenPaddingSides = 12.dp
    val ScreenPaddingTop = 16.dp
    val ScreenPaddingBottom = 16.dp

    // ==================== CommonCard ====================
    val CommonCardPaddingTop = 0.dp
    val CommonCardPaddingBottom = 0.dp
    val CommonCardContentPadding = 16.dp  // Единый отступ со всех сторон (как в Dashboard)
    val CommonCardCornerRadius = 16.dp  // Как в Dashboard (было 28.dp)
    val CommonCardElevation = 0.dp  // Тени не используем (Material Design 3 + iOS compatibility)
    val CommonCardBorderWidth = 1.dp  // Граница как в Dashboard (было 0.dp)
    val CommonCardAlpha = 1f

    // ==================== Card (общие карточки) ====================
    val CardCornerRadius = 16.dp
    val CardElevation = 0.dp  // Тени не используем (Material Design 3 + iOS compatibility)
    val CardPadding = 16.dp
    val CardItemSpacing = 12.dp

    // ==================== CommonButton ====================
    val CommonButtonHeight = 48.dp
    val CommonButtonCornerRadius = 24.dp

    // ==================== Spacing (универсальные отступы) ====================
    val SpaceSmall = 8.dp
    val SpaceMedium = 16.dp
    val SpaceLarge = 24.dp
    val SpaceExtraLarge = 32.dp

    // ==================== Components ====================
    val ChipHeight = 40.dp
    val IconSizeSmall = 20.dp  // Маленькие иконки (спиннеры, trailing icons)
    val IconSizeMedium = 24.dp  // Средние иконки (обычные действия)
    val IconSizeLarge = 28.dp  // Большие иконки (заголовки секций)
    val ProgressIndicatorStrokeWidth = 2.dp

    // ==================== Input Fields ====================
    val InputFieldCornerRadius = 8.dp  // TextField, Button и т.д.

    // ==================== Badge ====================
    val BadgeCornerRadius = 8.dp
    val BadgePaddingHorizontal = 8.dp
    val BadgePaddingVertical = 4.dp

    // ==================== Photo / Image ====================
    val PhotoPreviewHeight = 200.dp
    val PhotoPreviewCornerRadius = 8.dp

    // ==================== Other ====================
    val BorderWidthStandard = 1.dp  // Стандартная толщина границы

    // ==================== Text Sizes ====================
    val TextSizeTitle = 20.sp
    val TextSizeBody = 16.sp
}
