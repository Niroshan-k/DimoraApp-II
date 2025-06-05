package com.example.dimoraapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dimoraapp.data.repositor.AdvertisementRepository

class AdvertisementViewModelFactory(
    private val advertisementRepository: AdvertisementRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdvertisementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdvertisementViewModel(advertisementRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}