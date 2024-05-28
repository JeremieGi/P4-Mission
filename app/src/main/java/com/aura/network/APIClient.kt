package com.aura.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIClient {

//    // Ceci passe en paramètre :id=1234&password=xxx c'est pas ce que je veux
//    @POST("login") // EndPoint - Login
//    suspend fun postLogin(
//        @Query(value = "id") id : String,
//        @Query(value = "password") password: String
//    ): Response<APILoginResponse>

    @POST("login")
    suspend fun login(@Body jsonPostValue: LoginPostValue): Response<APILoginResponse> // L'objet renvoyé est un objet de type Response avec pour paramètre de généricité le type d'objet encapsulé dans cette réponse.
}