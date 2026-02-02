package org.igo.mycorc.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.igo.mycorc.ui.theme.LocalAppStrings

/**
 * Диалог подтверждения выхода из приложения.
 *
 * @param showDialog Показывать ли диалог.
 * @param onDismiss Callback при закрытии (нажата "Отмена" или тап вне диалога).
 * @param onConfirmExit Callback при подтверждении выхода.
 */
@Composable
fun ExitDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmExit: () -> Unit
) {
    if (!showDialog) return

    val strings = LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = strings.exitDialogTitle) },
        text = { Text(text = strings.exitDialogMessage) },
        confirmButton = {
            TextButton(onClick = onConfirmExit) {
                Text(
                    text = strings.exitDialogConfirm,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = strings.exitDialogCancel)
            }
        }
    )
}
