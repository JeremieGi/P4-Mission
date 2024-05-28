package com.aura.network

import com.aura.model.ModelResponseLogin
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Réponse de l'APi de login
 */
@JsonClass(generateAdapter = true) // indique la génération automatique d'un adaptateur JSON pour ces classes.
data class APIResponseLogin (

    @Json(name = "granted")
    val bGranted : Boolean

){
    /**
     * Transform in model object
     */
    fun toDomainModel() : ModelResponseLogin {
        return ModelResponseLogin(bGranted)
    }
}
