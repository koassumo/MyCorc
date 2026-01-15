package org.igo.mycorc.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun httpEngine(): HttpClientEngine = Darwin.create()
