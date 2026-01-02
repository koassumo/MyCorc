package org.igo.mycorc.di

import org.igo.mycorc.data.repository.NoteRepositoryImpl
import org.igo.mycorc.domain.repository.NoteRepository
import org.koin.dsl.module

val dataModule = module {
    // Явно говорим: "Когда просят NoteRepository, создай NoteRepositoryImpl"
    single<NoteRepository> { NoteRepositoryImpl() }
}
