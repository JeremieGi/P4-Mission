package com.aura.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface APIClient {

//    // Ceci passe en paramètre :id=1234&password=xxx c'est pas ce que je veux
//    @POST("login") // EndPoint - Login
//    suspend fun postLogin(
//        @Query(value = "id") id : String,
//        @Query(value = "password") password: String
//    ): Response<APILoginResponse>

    @POST("login")
    suspend fun login(@Body request: LoginPostValue): Response<APILoginResponse>
}