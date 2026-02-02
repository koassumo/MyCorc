package org.igo.mycorc.ui.screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getActivityContext(): Any? {
    return LocalContext.current
}
