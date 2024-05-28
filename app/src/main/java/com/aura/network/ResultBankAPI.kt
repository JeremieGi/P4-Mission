package com.aura.network

/**
 * Structuration de la réponse de l'API (pour tous les appels)
 * Utilisé dans le flow qui permet la communication entre le ViewModel et le repository
 */
sealed class ResultBankAPI<out T> {

    object Loading : ResultBankAPI<Nothing>()

    // C'est une classe de données qui représente l'état où l'opération a échoué. Elle peut contenir un message décrivant l'erreur survenue.
    data class Failure(
        val message: String? = null,
    ) : ResultBankAPI<Nothing>()

    // C'est une classe de données générique qui stocke le résultat de l'opération en cas de succès.
    // Elle prend un type générique R pour permettre de représenter différents types de résultats.
    data class Success<out R>(
        val value: R // Permet de récupérer les valeurs de ResultBankAPI
    ) : ResultBankAPI<R>()

}
