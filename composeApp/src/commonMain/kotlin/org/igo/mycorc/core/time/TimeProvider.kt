package org.igo.mycorc.core.time

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface TimeProvider {
    fun now(): Instant
    fun nowEpochMillis(): Long
    fun nowEpochSeconds(): Long
}

@OptIn(ExperimentalTime::class)
class SystemTimeProvider : TimeProvider {

    //UI/модель “создано в момент” — Instant (now()).
    override fun now(): Instant = Clock.System.now()

    //Время сервера Auth-токены/expiry — milliseconds
    //БД и Firestore
    override fun nowEpochMillis(): Long =
        now().toEpochMilliseconds()

    //Время сервера Auth-токены/expiry — seconds
    override fun nowEpochSeconds(): Long =
        nowEpochMillis() / 1000L
}
