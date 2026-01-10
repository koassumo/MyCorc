package org.igo.mycorc.domain.model

import kotlinx.serialization.Serializable

enum class NoteStatus {
    DRAFT,
    READY_TO_SEND,
    SENT,
    APPROVED,
    REJECTED
}

@Serializable
data class NotePayload(
    val step: String = "START",
    val biomass: BiomassData? = null,
    val coal: CoalData? = null,
    val packaging: PackagingData? = null,
    val locationComment: String? = null
)

@Serializable
data class BiomassData(
    val weight: Double,
    val type: String,
    val photoPath: String? = null
)

@Serializable
data class CoalData(
    val weight: Double,
    val quality: String,
    val photoPath: String? = null
)

@Serializable
data class PackagingData(
    val bagCount: Int,
    val photoPath: String? = null
)