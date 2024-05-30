package com.aura.repository

import com.aura.model.ModelResponseAccount
import com.aura.model.ModelResponseLogin
import com.aura.model.ModelResponseTransfer
import com.aura.network.APIClient
import com.aura.network.APIResponseAccount
import com.aura.network.LoginPostValue
import com.aura.network.ResultBankAPI
import com.aura.network.TransferPostValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class BankRepository(
    private val dataService: APIClient
) {

    // T002 - Plug the API on the login screen
    /**
     * Appelle l'API de login et emet les résultats dans un flow qui sera lu côté UI
     */
    fun login(sLoginP : String, sPasswordP : String) : Flow<ResultBankAPI<ModelResponseLogin>> = flow {

        emit(ResultBankAPI.Loading)

        // Appel à l'API
        val postValues = LoginPostValue(sLoginP,sPasswordP)
        val result = dataService.login(postValues)
        // si la requête met du temps, pas grave, on est dans une coroutine, le thread principal n'est pas bloqué

        // Transformation du résultat en données du Model
        val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")

        // Ajout au flow
        emit(ResultBankAPI.Success(model))

    }.catch { error ->
        emit(ResultBankAPI.Failure(error.message+" "+error.cause?.message)) // Message enrichi
    }

    /**
     * Appelle l'API permettant de lister les comptes d'un utilisateur
     * et emet les résultats dans un flow qui sera lu côté UI
     */
    fun accounts(sUserIDP: String) : Flow<ResultBankAPI<List<ModelResponseAccount>>> = flow {

        emit(ResultBankAPI.Loading)

        // Appel à l'API
        //val resultAPIListAccount = dataService.accounts(sUserIDP).body()
        val responseRetrofit = dataService.accounts(sUserIDP)

        // si la requête met du temps, pas grave, on est dans une coroutine

        if (responseRetrofit.isSuccessful){

            val resultAPIListAccount = responseRetrofit.body()

            // TODO : Revoir cette transformation en données du modèle (peut-être revoir le type de retour de la méthode accounts)
            val resultModelAPIListAccount = APIResponseAccount.toListDomainModel(resultAPIListAccount)

            // Ajout au flow
            emit(ResultBankAPI.Success(resultModelAPIListAccount))


        }
        else{
            // TODO : J'ai été obligé d'ajouter ce cas pour que le test NetworkProblem fonctionne
            // Je pensais que dataService.accounts(sUserIDP) allait lever une Exception et allait terminer dans le catch
            emit(ResultBankAPI.Failure("Error code ${responseRetrofit.code()}"))
        }


    }.catch { error ->
        emit(ResultBankAPI.Failure(error.message+" "+error.cause?.message)) // Message enrichi
    }

    /**
     * Appelle l'API permettant d'effectuer des virements (transfert d'argent)
     */
    fun transfer(sUserSenderP: String, sUserRecipientP: String, dAmountP : Double) : Flow<ResultBankAPI<ModelResponseTransfer>> = flow {

        emit(ResultBankAPI.Loading)

        // Appel à l'API
        val postValues = TransferPostValue(sUserSenderP,sUserRecipientP,dAmountP)
        val result = dataService.transfer(postValues)
        // si la requête met du temps, pas grave, on est dans une coroutine, le thread principal n'est pas bloqué

        // Transformation du résultat en données du Model
        // Si l'ID du recipient est pas correct le WS renvoie une erreur 500
        val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data \n Check the recipent ID")

        // Ajout au flow
        emit(ResultBankAPI.Success(model))
    }.catch { error ->
        emit(ResultBankAPI.Failure(error.message + " " + error.cause?.message)) // Message enrichi
    }

}

