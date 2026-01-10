package org.igo.mycorc.di

import org.igo.mycorc.data.repository.NoteRepositoryImpl
import org.igo.mycorc.data.repository.SettingsRepositoryImpl
import org.igo.mycorc.domain.rep_interface.NoteRepository
import org.igo.mycorc.domain.rep_interface.SettingsRepository
import org.koin.dsl.module

val dataModule = module {
    // Явно говорим: "Когда просят NoteRepository, создай NoteRepositoryImpl"
    single<NoteRepository> { NoteRepositoryImpl() }

    // Koin: "Ага, SettingsRepositoryImpl просит settings.
    // Я найду settings в platformModule и отдам ему (get())"
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}
