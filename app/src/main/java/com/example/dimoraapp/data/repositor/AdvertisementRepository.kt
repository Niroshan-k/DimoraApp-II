package com.example.dimoraapp.data.repositor

import JetstreamApi
import com.example.dimoraapp.model.AdvertisementApi
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

    suspend fun getAdvertisementById(id1: String, id: Int): Response<AdvertisementApiResponse> {
        val token = sessionManager.getToken()
        require(!token.isNullOrEmpty()) { "Token is missing" }
        return api.getAdvertisementById("Bearer $token", id)
    }

    suspend fun searchAdvertisements(query: String): Response<List<AdvertisementApi>> {
        val token = sessionManager.getToken()
        require(!token.isNullOrEmpty()) { "Token is missing" }
        return api.searchAdvertisements(query, "Bearer $token")
    }
}