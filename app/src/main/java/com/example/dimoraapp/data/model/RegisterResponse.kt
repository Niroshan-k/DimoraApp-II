package com.example.dimoraapp.data.model

data class RegisterResponse(
    val user: User,
    val token: String
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)
