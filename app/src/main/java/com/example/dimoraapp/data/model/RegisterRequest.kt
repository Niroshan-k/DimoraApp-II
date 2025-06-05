package com.example.dimoraapp.data.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val contact_number: String? = null // Optional field for contact number
)
