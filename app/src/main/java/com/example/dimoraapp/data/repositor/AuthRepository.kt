package com.example.dimoraapp.data.repositor

import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.data.model.RegisterRequest
import com.example.dimoraapp.data.model.RegisterResponse
import retrofit2.Response

class AuthRepository {

    suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        return RetrofitClient.api.register(request)
    }

}

