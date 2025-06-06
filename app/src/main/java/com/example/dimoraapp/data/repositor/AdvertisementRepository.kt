package com.example.dimoraapp.data.repositor

import com.example.dimoraapp.data.api.JetstreamApi
import com.example.dimoraapp.model.AdvertisementApiResponse
import com.example.dimoraapp.model.AdvertisementListResponse
import com.example.dimoraapp.utils.SessionManager
import retrofit2.Response

class AdvertisementRepository(
    private val api: JetstreamApi,
    private val sessionManager: SessionManager
) {
    suspend fun getAdvertisements(): Response<AdvertisementListResponse> {
        val token = sessionManager.getToken()
        require(!token.isNullOrEmpty()) { "Token is missing" }
        return api.getAdvertisements("Bearer $token")
    }

    suspend fun getAdvertisementById(token: String, id: Int): Response<AdvertisementApiResponse> {
        return api.getAdvertisementById(token, id)
    }
}