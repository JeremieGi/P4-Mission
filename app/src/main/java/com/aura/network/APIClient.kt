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
     * qui renvoie :
     * [
     *     {
     *         "id": "1",
     *         "main": true,
     *         "balance": 2354.23
     *     },
     *     {
     *         "id": "2",
     *         "main": false,
     *         "balance": 235.22
     *     }
     * ]
     */
    @GET("accounts/{userId}")
    suspend fun getAccountDetails(@Path("userId") userId: String): Response<List<APIResponseAccount>>

}