package com.example.dimoraapp.data.repositor

import android.util.Log
import com.example.dimoraapp.data.api.JetstreamApi
import com.example.dimoraapp.data.model.RegisterRequest
import com.example.dimoraapp.data.model.RegisterResponse
import com.example.dimoraapp.data.model.ProfileResponse
import com.example.dimoraapp.utils.SessionManager
import retrofit2.Response

class AuthRepository(
    private val api: JetstreamApi,
    private val sessionManager: SessionManager
) {

    suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        return api.register(request)
    }

    suspend fun getProfile(): Response<ProfileResponse> {
        val token = sessionManager.getToken()
        Log.d("AuthRepository", "Extracted token: $token")
        require(!token.isNullOrEmpty()) { "Token is missing" }

        return try {
            Log.d("AuthRepository", "Making API call with token: Bearer $token")
            val response = api.getProfile("Bearer $token")

            // Log the raw response (both successful and error cases)
            if (response.isSuccessful) {
                Log.d("AuthRepository", "Raw Response Body: ${response.body()?.toString()}")
            } else {
                Log.e("AuthRepository", "Raw Error Body: ${response.errorBody()?.string()}")
            }

            response
        } catch (e: Exception) {
            Log.e("AuthRepository", "Exception: ${e.message}")
            throw Exception("Failed to fetch profile: ${e.message}", e)
        }
    }
}