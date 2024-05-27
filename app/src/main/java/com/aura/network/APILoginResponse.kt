package com.aura.network

import com.aura.model.LoginReportModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Réponse de l'APi de login
 */
@JsonClass(generateAdapter = true) // indique la génération automatique d'un adaptateur JSON pour ces classes.
data class APILoginResponse (

    @Json(name = "granted")
    val bGranted : Boolean

){
    /**
     * Transform in model object
     */
    fun toDomainModel() : LoginReportModel {
        return LoginReportModel(bGranted)
    }
}
