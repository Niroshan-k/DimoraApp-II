package com.example.dimoraapp.data.api

import com.example.dimoraapp.data.model.RegisterRequest
import com.example.dimoraapp.data.model.RegisterResponse
import com.example.dimoraapp.data.model.ProfileResponse
import com.example.dimoraapp.model.AdvertisementListResponse
import com.example.dimoraapp.model.AdvertisementApiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


interface JetstreamApi {

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    // Profile API
    @GET("api/user") // Corrected endpoint path
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @GET("api/advertisement")
    suspend fun getAdvertisements(@Header("Authorization") token: String): Response<AdvertisementListResponse>

    @GET("api/advertisement/{id}")
    suspend fun getAdvertisementById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<AdvertisementApiResponse>
}