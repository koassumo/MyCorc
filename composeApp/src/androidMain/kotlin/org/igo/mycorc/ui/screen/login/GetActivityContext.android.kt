package org.igo.mycorc.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getActivityContext(): Any? {
    return LocalContext.current
}
