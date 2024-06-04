package com.aura.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIClient {

//    // Ceci passe en paramètre :id=1234&password=xxx c'est pas ce que je veux
//    @POST("login") // EndPoint - Login
//    suspend fun postLogin(
//        @Query(value = "id") id : String,
//        @Query(value = "password") password: String
//    ): Response<APILoginResponse>

    /**
     * Equivalent de cet appel : ..../login
     * Avec valeur postée :
     *  {
     *   "id": "1234",
     *   "password": "p@sswOrd"
     * }
     * et la réponse :
     * {
     *     "granted": true
     * }
     */
    @POST("login")
    suspend fun login(@Body jsonPostValue: LoginPostValue): Response<APIResponseLogin> // L'objet renvoyé est un objet de type Response avec pour paramètre de généricité le type d'objet encapsulé dans cette réponse.

    /**
     * Equivalent de cet appel ..../accounts/1234
     * Avec valeur postée :
     * {
     *   "sender": "5678",
     *   "recipient": "1234",
     *   "amount": 10
     * }
     * qui renvoie :
     * {
     *     "result": true
     * }
     * T011 - Plug the API on the transfer screen
     */
    // Response<List<APIResponseAccount>> => ok
    // j'ai l'impression que çà me complique le code
    @GET("accounts/{userId}")
    suspend fun accounts(@Path("userId") userId: String): Response<List<APIResponseAccount>>

    /**
     * Equivalent de cet appel ..../transfer
     * qui renvoie :
     */
    @POST("transfer")
    suspend fun transfer(@Body jsonPostValue: TransferPostValue): Response<APIResponseTransfer> // L'objet renvoyé est un objet de type Response avec pour paramètre de généricité le type d'objet encapsulé dans cette réponse.


}