package com.aura.model

/**
 * Compte tel qu'il est renvoyé par l'API
 */
data class ModelResponseAccount(

    val sAccountID: String = "",
    val bMainAccount : Boolean = false,
    val dBalance : Double = 0.0

)
