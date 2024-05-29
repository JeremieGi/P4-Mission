package com.aura.network

/**
 * Contenu du post JSON effectué pour un transfert d'argent
 */
data class TransferPostValue(
    val sender: String,
    val recipient: String,
    val amount: Double
)