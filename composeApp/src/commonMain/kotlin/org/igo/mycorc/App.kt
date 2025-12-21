package org.igo.mycorc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.igo.mycorc.ui.common.CommonCard // Импорт нашей карточки

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxSize()) {

            // Наша первая карточка
            CommonCard(onClick = { println("Clicked!") }) {
                Text(
                    text = "Партия угля #1",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "Вес: 500 кг")
                Text(text = "Статус: На проверке")
            }

            // Вторая карточка
            CommonCard {
                Text(
                    text = "Создать новую запись",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}