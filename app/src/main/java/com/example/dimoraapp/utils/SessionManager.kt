package com.example.dimoraapp.utils

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // Save user credentials or session token
    fun saveSession(email: String?, loginResponseJson: String?) {
        val token = if (loginResponseJson != null) {
            try {
                val jsonObject = JSONObject(loginResponseJson)
                jsonObject.getString("token") // Extract the token from the JSON
            } catch (e: Exception) {
                null // Handle JSON parsing errors gracefully
            }
        } else {
            null
        }

        // Save email and token
        editor.putString("EMAIL", email)
        editor.putString("TOKEN", token)
        editor.putLong("SESSION_START_TIME", System.currentTimeMillis())
        editor.apply()
    }
    private val THREE_DAYS_IN_MILLIS = 3 * 24 * 60 * 60 * 1000 // 3 days in milliseconds
    // Check if the session is still valid (3 days = 259200000 milliseconds)
    fun isSessionValid(): Boolean {
        val sessionStartTime = sharedPreferences.getLong("SESSION_START_TIME", 0)
        val currentTime = System.currentTimeMillis()
        return (currentTime - sessionStartTime) <= THREE_DAYS_IN_MILLIS// 3 days in milliseconds
    }

    // Get saved email
    fun getEmail(): String? = sharedPreferences.getString("EMAIL", null)

    // Get saved token
    fun getToken(): String? = sharedPreferences.getString("TOKEN", null)

    // Clear session
    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}