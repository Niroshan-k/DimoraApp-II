package com.example.dimoraapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimoraapp.data.repositor.NotificationRepository
import com.example.dimoraapp.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val token: String
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _notificationCount = MutableStateFlow(0)
    val notificationCount: StateFlow<Int> = _notificationCount

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val result = repository.getNotifications(token)
                if (result != null) {
                    _notifications.value = result
                    _notificationCount.value = result.size // <-- Update badge count here!
                } else {
                    _error.value = "Failed to load notifications: Empty or invalid data."
                    _notificationCount.value = 0           // <-- Reset count on error
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred. Please try again later."
                _notificationCount.value = 0               // <-- Reset count on exception
            }
            _loading.value = false
        }
    }

    fun clearNotificationCount() {
        _notificationCount.value = 0
    }

    companion object
}