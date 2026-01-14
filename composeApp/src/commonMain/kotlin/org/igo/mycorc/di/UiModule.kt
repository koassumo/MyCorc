//UiModule.kt
package org.igo.mycorc.di

// 👇 ЭТИ ДВА ИМПОРТА КРИТИЧЕСКИ ВАЖНЫ ДЛЯ viewModelOf
// Не удаляйте их, даже если студия пишет, что они "Unused" (не используются)
import org.igo.mycorc.domain.usecase.SaveNoteUseCase
import org.igo.mycorc.data.local.ImageStorage
//
import org.igo.mycorc.ui.screen.auth.LoginViewModel
import org.igo.mycorc.ui.screen.main.MainViewModel
import org.igo.mycorc.ui.screen.dashboard.DashboardViewModel
import org.igo.mycorc.ui.screen.settings.SettingsViewModel
import org.igo.mycorc.ui.screen.create.CreateNoteViewModel
import org.igo.mycorc.ui.screen.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::SettingsViewModel)
    // не удалять импорты выше!!!
    viewModelOf(::CreateNoteViewModel)
    viewModelOf(::ProfileViewModel)


}