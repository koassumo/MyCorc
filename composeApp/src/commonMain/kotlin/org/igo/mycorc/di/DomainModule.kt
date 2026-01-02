package org.igo.mycorc.di

import org.igo.mycorc.domain.usecase.GetNoteDetailsUseCase
import org.igo.mycorc.domain.usecase.GetNoteListUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    // UseCase обычно не хранят состояние, поэтому используем factory (новый объект каждый раз)
    factoryOf(::GetNoteListUseCase)
    factoryOf(::GetNoteDetailsUseCase)
}
