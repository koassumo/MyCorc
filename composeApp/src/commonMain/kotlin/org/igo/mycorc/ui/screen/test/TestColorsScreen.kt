package org.igo.mycorc.ui.screen.test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.igo.mycorc.ui.common.CommonCard
import org.igo.mycorc.ui.common.Dimens
import org.igo.mycorc.ui.common.LocalTopBarState

/**
 * Тестовый экран для визуализации цветовой системы Material3.
 * Каждая надпись показывает название семантического цвета, которым она окрашена.
 * Формат: "onSurface ← surface" (цвет текста ← цвет фона).
 */
@Composable
fun TestColorsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val topBar = LocalTopBarState.current

    // Публикуем конфигурацию TopBar
    topBar.title = "onSurface     ← surface"  // TopBar использует surface фон + onSurface текст
    topBar.canNavigateBack = true
    topBar.onNavigateBack = onNavigateBack  // ← FIX: явно устанавливаем callback

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPaddingSides)
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

        // ==========================================
        // PRIMARY (основной брендовый цвет)
        // ==========================================
        SectionTitle("PRIMARY COLORS")

        // FilledButton: primary фон + onPrimary текст
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("onPrimary")
                Text("← primary", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

        // FilledTonalButton: primaryContainer фон + onPrimaryContainer текст
        FilledTonalButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("onPrimaryContainer")
                Text("← primaryContainer", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

        // OutlinedButton: transparent фон + primary текст + primary обводка
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("primary")
                Text("outline: primary", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // ==========================================
        // SECONDARY (дополнительный цвет)
        // ==========================================
        SectionTitle("SECONDARY COLORS")

        // FilterChip: secondary фон когда selected
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
        ) {
            FilterChip(
                selected = true,
                onClick = {},
                label = {
                    Column {
                        Text("onSecondary", style = MaterialTheme.typography.bodySmall)
                        Text("← secondary", style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
            FilterChip(
                selected = false,
                onClick = {},
                label = {
                    Column {
                        Text("onSurfaceVariant", style = MaterialTheme.typography.bodySmall)
                        Text("← surfaceVariant", style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // ==========================================
        // SURFACE (поверхности)
        // ==========================================
        SectionTitle("SURFACE COLORS")

        // Card (Material3 default): surface фон + onSurface текст
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.CardCornerRadius),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(Dimens.BorderWidthStandard, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.CardPadding)
            ) {
                Text(
                    text = "onSurface",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "← surface",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))
                Text(
                    text = "outline (border)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // ==========================================
        // BACKGROUND
        // ==========================================
        SectionTitle("BACKGROUND COLORS")

        Column {
            Text(
                text = "onBackground",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "← background (screen background)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // ==========================================
        // ERROR (ошибки)
        // ==========================================
        SectionTitle("ERROR COLORS")

        // Error button
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("onError")
                Text("← error", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

        // Error container (light error background)
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(Dimens.InputFieldCornerRadius),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Dimens.SpaceMedium)) {
                Text(
                    text = "onErrorContainer",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "← errorContainer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // ==========================================
        // TEXT FIELD
        // ==========================================
        SectionTitle("TEXT FIELD COLORS")

        OutlinedTextField(
            value = "onSurface",
            onValueChange = {},
            label = { Text("onSurfaceVariant") },
            placeholder = { Text("onSurfaceVariant") },
            supportingText = {
                Column {
                    Text("Label, placeholder, hint:", style = MaterialTheme.typography.labelSmall)
                    Text("onSurfaceVariant", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // ==========================================
        // DIVIDERS
        // ==========================================
        SectionTitle("DIVIDER COLORS")

        HorizontalDivider()
        Spacer(modifier = Modifier.height(Dimens.SpaceSmall))
        Column {
            Text(
                text = "↑ Divider color:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "outlineVariant",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // ==========================================
        // FAB (FloatingActionButton)
        // ==========================================
        SectionTitle("FAB COLORS")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FloatingActionButton(
                onClick = {}
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceSmall))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "↑ FAB icon: onPrimaryContainer",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "← primaryContainer",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // ==========================================
        // TEXT HIERARCHY
        // ==========================================
        SectionTitle("TEXT HIERARCHY")

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium)
        ) {
            Column {
                Text(
                    text = "onSurface",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "(primary text)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column {
                Text(
                    text = "onSurfaceVariant",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "(secondary text)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column {
                Text(
                    text = "outline",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "(tertiary/border color)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // ==========================================
        // BOTTOM NAVIGATION BAR (указатель вниз на реальный BottomBar)
        // ==========================================
        HorizontalDivider()
        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "↓ ↓ ↓ BOTTOM NAVIGATION BAR ↓ ↓ ↓",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Container:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "surfaceContainer",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                Text(
                    text = "Selected item:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "onSecondaryContainer",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "← secondaryContainer",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

                Text(
                    text = "Unselected item:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "onSurfaceVariant",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = Dimens.SpaceSmall)
    )
}
