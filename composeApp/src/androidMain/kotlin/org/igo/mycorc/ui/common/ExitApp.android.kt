package org.igo.mycorc.ui.common

import android.os.Process

actual fun exitApp() {
    Process.killProcess(Process.myPid())
}
