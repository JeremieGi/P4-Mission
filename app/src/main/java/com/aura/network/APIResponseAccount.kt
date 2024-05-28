package com.aura.network

import com.aura.model.ModelResponseAccount
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * {
 *         "id": "1",
 *         "main": true,
 *         "balance": 2354.23
 *     }
 */

@JsonClass(generateAdapter = true)
data class APIResponseAccount(

    @Json(name = "id")
    val sAccountID: String,

    @Json(name = "main")
    val bMainAccount : Boolean,

    @Json(name = "balance")
    val dBalance : Double

){
    /**
     * Transform in model object
     */
    fun toDomainModel() : ModelResponseAccount {

        // TODO : Est ce que la classe ModelResponseAccount est vraiment utile ici ? car équivalente à APIResponseAccount
        return ModelResponseAccount(
            sAccountID = sAccountID,
            bMainAccount = bMainAccount,
            dBalance = dBalance
        )

    }

    companion object {
        /**
         * Transform a list of APiResponse in ModelResponse
         */
        fun toListDomainModel(resultAPIListAccount: List<APIResponseAccount>?): List<ModelResponseAccount> {

            // TODO : Ici je peux mettre val alors que j'ajoute des éléments dans la liste
            val result = mutableListOf<ModelResponseAccount>()
            resultAPIListAccount?.forEach {
                result.add(it.toDomainModel())
            }
            return result

        }
    }
}
