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

class SignUpViewModel : ViewModel() {

    private val client = OkHttpClient()

    fun signUp(
        email: String,
        username: String,
        contact: String, // Jetstream doesn't use this, but kept in case you store it later
        password: String,
        confirmPassword: String,
        user_role: String = "buyer", // Default to "buyer" as per your requirement
        onResult: (Boolean, String) -> Unit
    ) {
        val url = "http://10.0.2.2:8000/api/register" // Jetstream's default register endpoint
        Log.d("SignUpViewModel", "signUp function called")
        // JSON body matching Jetstream expectations
        val json: JSONObject
        try {
            json = JSONObject().apply {
                put("name", username)
                put("email", email)
                put("contact_number", contact)
                put("password", password)
                put("password_confirmation", confirmPassword)
                put("user_role", "buyer")
            }
            //Log.d("SignUpViewModel", "Request JSON: ${json.toString()}")
        } catch (e: Exception) {
            Log.e("SignUpViewModel", "Error creating JSON", e)
            return // Exit the function if JSON creation fails
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
            Log.d("SignUpViewModel", "Request JSON: $json")
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("SignUpViewModel", "Network request failed", e)
                    viewModelScope.launch(Dispatchers.Main) {
                        onResult(false, "Network error: ${e.localizedMessage}")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string() // Fixed field access
                    Log.d("SignUpViewModel", "Response: $responseBody")
                    viewModelScope.launch(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            onResult(true, responseBody ?: "Success")
                        } else {
                            onResult(false, responseBody ?: "Unknown error")
                        }
                    }
                }
            })
        }
    }
}