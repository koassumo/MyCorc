//DomainModule.kt

package org.igo.mycorc.di

import org.igo.mycorc.domain.usecase.GetNoteByIdUseCase
import org.igo.mycorc.domain.usecase.GetNoteListUseCase
import org.igo.mycorc.domain.usecase.SaveNoteUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.igo.mycorc.domain.usecase.SyncNoteUseCase

val domainModule = module {
    // UseCase обычно не хранят состояние, поэтому используем factory (новый объект каждый раз)
    factoryOf(::GetNoteListUseCase)
    factoryOf(::GetNoteByIdUseCase)
    factoryOf(::SaveNoteUseCase)
    factoryOf(::SyncNoteUseCase)
}
