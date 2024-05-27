package com.aura.network

/**
 * Structuration de la réponse de l'API
 */
sealed class ResultBankAPI<out T> {

    object Loading : ResultBankAPI<Nothing>()

    // C'est une classe de données qui représente l'état où l'opération a échoué. Elle peut contenir un message décrivant l'erreur survenue.
    data class Failure(
        val message: String? = null,
    ) : ResultBankAPI<Nothing>()

    // C'est une classe de données générique qui stocke le résultat de l'opération en cas de succès.
    // Elle prend un type générique R pour permettre de représenter différents types de résultats.
    data class Success<out R>(val value: R) : ResultBankAPI<R>()

}
