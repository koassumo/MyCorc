package org.igo.mycorc.ui.screen.dashboard

import org.igo.mycorc.domain.model.Note

data class DashboardState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList()
)