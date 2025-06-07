package com.example.dimoraapp.model

data class ServerErrorMessage(
    val title: String,
    val message: String,
    val imageAsset: String // path in assets or drawable name
)