package org.igo.mycorc.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotePayload(
    val step: String = "DRAFT",   // Этап: BIOMASS, COAL, COMPLETED
    val biomass: BiomassData? = null,
    val coal: CoalData? = null,
    val packaging: PackagingData? = null,
    val locationComment: String? = null
)

@Serializable
data class BiomassData(
    val weight: Double = 0.0,
    val photoPath: String = "",     // Локальный путь или путь в Storage
    val photoUrl: String = ""       // Публичная ссылка с сервера
)

@Serializable
data class CoalData(
    val weight: Double = 0.0,
    val type: String = ""
)

@Serializable
data class PackagingData(
    val count: Int = 0
)