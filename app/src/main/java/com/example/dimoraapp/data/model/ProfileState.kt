package com.example.dimoraapp.data.model

data class ProfileState(
    val email: String = "",
    val username: String = "",
    val contact: String? = "",
    val isLoading: Boolean,
    val error: String?
)