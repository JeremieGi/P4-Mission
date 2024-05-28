package com.aura.repository

import com.aura.model.LoginReportModel
import com.aura.network.APIClient
import com.aura.network.LoginPostValue
import com.aura.network.ResultBankAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class BankRepository(
    private val dataService: APIClient
) {

    // T002 - Plug the API on the login screen
    /**
     * Appelle l'API et emet les résultats dans un flow qui sera lu côté UI
     */
    fun login(sLoginP : String, sPasswordP : String) : Flow<ResultBankAPI<LoginReportModel>> = flow {

        emit(ResultBankAPI.Loading)

        // Appel à l'API
        val postValues = LoginPostValue(sLoginP,sPasswordP)
        val result = dataService.login(postValues)
        // si la requête met du temps, pas grave, on est dans une coroutinre, le thread principal n'est pas bloqué

        // Transformation du résultat en données du Model
        val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")

        // Ajout au flow
        emit(ResultBankAPI.Success(model))

    }.catch { error ->
        emit(ResultBankAPI.Failure(error.message+" "+error.cause?.message)) // Message enrichi
    }

}