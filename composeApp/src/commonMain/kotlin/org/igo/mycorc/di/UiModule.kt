package org.igo.mycorc.di

import org.igo.mycorc.ui.screen.dashboard.DashboardViewModel
import org.igo.mycorc.ui.screen.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    // Koin сам посмотрит в конструктор NoteListViewModel, увидит, что там нужно,
    // найдет это в других модулях и подставит.
    viewModelOf(::DashboardViewModel)
    viewModelOf(::SettingsViewModel)
}


