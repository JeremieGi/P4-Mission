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

            val result = mutableListOf<ModelResponseAccount>()
            resultAPIListAccount?.forEach {
                result.add(it.toDomainModel())
            }
            return result

        }
    }
}
