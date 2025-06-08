package com.example.dimoraapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimoraapp.data.repositor.AdvertisementRepository
import com.example.dimoraapp.model.AdvertisementApi
import kotlinx.coroutines.launch

class SearchViewModel(private val repo: AdvertisementRepository) : ViewModel() {
    var results by mutableStateOf<List<AdvertisementApi>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun search(query: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = repo.searchAdvertisements(query)
                if (response.isSuccessful) {
                    results = response.body() ?: emptyList()
                } else {
                    error = response.errorBody()?.string() ?: "Unknown error"
                }
            } catch (e: Exception) {
                error = e.message
            }
            isLoading = false
        }
    }
}