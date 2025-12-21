package org.igo.mycorc

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform