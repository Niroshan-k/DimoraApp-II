package com.example.dimoraapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dimoraapp.data.repositor.AdvertisementRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.dimoraapp.model.AdvertisementApi
import com.example.dimoraapp.model.AdvertisementApiResponse
import com.example.dimoraapp.utils.SessionManager

class InfoViewModel(
    private val advertisementRepository: AdvertisementRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _ad = mutableStateOf<AdvertisementApi?>(null)
    val ad: State<AdvertisementApi?> = _ad

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchAdvertisementById(adId: Int) {
        viewModelScope.launch {
            try {
                val token = sessionManager.getToken()
                val response: Response<AdvertisementApiResponse> = advertisementRepository.getAdvertisementById("Bearer $token", adId)
                if (response.isSuccessful) {
                    _ad.value = response.body()?.data
                    _error.value = null
                } else {
                    _error.value = "Server error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}

// ViewModel Factory for InfoViewModel
class InfoViewModelFactory(
    private val repository: AdvertisementRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InfoViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}