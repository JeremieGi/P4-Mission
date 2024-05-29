package com.aura.network

import com.aura.model.ModelResponseTransfer
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Réponse de l'APi de transfer
 */
@JsonClass(generateAdapter = true) // indique la génération automatique d'un adaptateur JSON pour ces classes.
data class APIResponseTransfer (

    @Json(name = "result")
    val bResult : Boolean

){
    /**
     * Transform in model object
     */
    fun toDomainModel() : ModelResponseTransfer {
        return ModelResponseTransfer(bResult)
    }
}