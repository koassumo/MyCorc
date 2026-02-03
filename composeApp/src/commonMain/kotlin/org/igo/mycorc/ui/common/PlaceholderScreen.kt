package org.igo.mycorc.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Универсальный placeholder экран для пустых/незаполненных разделов.
 * Показывает текст в центре экрана.
 */
@Composable
fun PlaceholderScreen(title: String) {
    Scaffold(
        topBar = { CommonTopBar(title = title, windowInsets = WindowInsets(0.dp)) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Coming soon...",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
