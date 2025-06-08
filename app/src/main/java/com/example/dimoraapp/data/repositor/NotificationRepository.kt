package com.example.dimoraapp.data.repositor

import JetstreamApi
import com.example.dimoraapp.model.Notification

class NotificationRepository(private val api: JetstreamApi) {
    suspend fun getNotifications(token: String): List<Notification>? {
        val response = api.getNotifications("Bearer $token")
        return if (response.isSuccessful) {
            response.body()?.notifications
        } else {
            null
        }
    }
}