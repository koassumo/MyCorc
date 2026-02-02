package org.igo.mycorc.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Универсальный placeholder экран для пустых/незаполненных разделов.
 * Показывает заголовок в центре экрана.
 */
@Composable
fun PlaceholderScreen(title: String) {
    Scaffold(
        topBar = { CommonTopBar(title = title) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
