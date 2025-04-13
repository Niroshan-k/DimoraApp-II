package com.example.dimoraapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class SignInViewModel : ViewModel() {

    private val client = OkHttpClient()

    fun signIn(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val url = "http://10.0.2.2:8000/api/login" // Change to your login API endpoint

        // JSON body for login request
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()

        viewModelScope.launch(Dispatchers.IO) {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("SignInViewModel", "Network request failed", e)
                    viewModelScope.launch(Dispatchers.Main) {
                        onResult(false, "Network error: ${e.localizedMessage}")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("SignInViewModel", "Response: $responseBody")
                    viewModelScope.launch(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            onResult(true, responseBody ?: "Success")
                        } else {
                            onResult(false, responseBody ?: "Invalid credentials")
                        }
                    }
                }
            })
        }
    }
}