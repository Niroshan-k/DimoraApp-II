package com.example.dimoraapp.data.api

import com.example.dimoraapp.data.model.RegisterRequest
import com.example.dimoraapp.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response


interface JetstreamApi {

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

}