package org.igo.mycorc.di

import org.igo.mycorc.ui.screen.dashboard.DashboardViewModel
import org.igo.mycorc.ui.screen.settings.SettingsViewModel
import org.igo.mycorc.ui.screen.create.CreateNoteViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.viewModel // Используем DSL для ручного создания
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::SettingsViewModel)

    // 👇 ДОБАВИЛИ <CreateNoteViewModel> (явно указываем тип)
    viewModel<CreateNoteViewModel> {
        CreateNoteViewModel(get(), get())
    }
    //сборка не проходит, пришлось через get прописать

}



