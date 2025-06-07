package com.example.dimoraapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimoraapp.data.model.ProfileState
import com.example.dimoraapp.data.repositor.AuthRepository
import com.example.dimoraapp.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: Application,
    private val repository: AuthRepository
) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    // StateFlow for UI state (profile loaded from API)
    private val _profileState = MutableStateFlow(
        ProfileState(
            isLoading = true,
            error = null
        )
    )
    val profileState: StateFlow<ProfileState> = _profileState

    // StateFlow for locally saved user profile (offline)
    val offlineProfile: StateFlow<ProfileState?> = dataStoreManager.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Save profile to DataStore
    fun saveProfileOffline(profile: ProfileState) {
        viewModelScope.launch {
            dataStoreManager.saveUserProfile(profile)
        }
    }

    // Fetch profile from server
    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            try {
                val response = repository.getProfile()
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        val state = ProfileState(
                            email = profile.email,
                            username = profile.name, // Update as needed
                            contact = profile.contactNumber, // Update as needed
                            isLoading = false,
                            error = null
                        )
                        _profileState.value = state
                    }
                } else {
                    _profileState.value = ProfileState(
                        isLoading = false,
                        error = "Error: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState(
                    isLoading = false,
                    error = "Exception: ${e.message}"
                )
            }
        }
    }
}