package com.example.dimoraapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimoraapp.data.repositor.AdvertisementRepository
import com.example.dimoraapp.entities.AdvertisementEntity
import com.example.dimoraapp.model.Advertisement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdvertisementViewModel(
    private val advertisementRepository: AdvertisementRepository
) : ViewModel() {
    var ads = mutableStateOf<List<Advertisement>>(emptyList())
        private set
    var error = mutableStateOf<String?>(null)
        private set

    fun fetchAdvertisements() {
        viewModelScope.launch {
            try {
                val response = advertisementRepository.getAdvertisements()
                if (response.isSuccessful) {
                    ads.value = response.body()?.data ?: emptyList()
                    error.value = null
                } else {
                    error.value = "Server error: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}

