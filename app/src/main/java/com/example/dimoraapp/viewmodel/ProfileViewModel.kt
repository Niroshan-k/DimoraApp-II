package com.example.dimoraapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimoraapp.data.model.ProfileState
import com.example.dimoraapp.data.repositor.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {

    // StateFlow for the profile state
    private val _profileState = MutableStateFlow(
        ProfileState(
            isLoading = true,
            error = null // Initial error is null
        )
    )
    val profileState: StateFlow<ProfileState> = _profileState

    // Fetch profile data from the repository
    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            try {
                val response = repository.getProfile()
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        _profileState.value = ProfileState(
                            email = profile.email,
                            username = profile.name, // Replace with actual property name
                            contact = profile.contactNumber, // Replace with actual property name
                            isLoading = false,
                            error = null // No error if successful
                        )
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