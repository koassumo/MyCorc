package org.igo.mycorc

import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.igo.mycorc.ui.screen.main.MainScreen

import org.igo.mycorc.ui.theme.MyAppTheme

@Composable
@Preview
fun App() {

    MyAppTheme {
        MainScreen()
    }
}