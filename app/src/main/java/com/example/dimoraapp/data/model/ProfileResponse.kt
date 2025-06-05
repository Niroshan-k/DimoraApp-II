package com.example.dimoraapp.data.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    //val id: Int,
    val email: String,
    val name: String, // This will be the username
    @SerializedName("contact_number") val contactNumber: String, // Matches "contact_number" in JSON
    //@SerializedName("profile_photo_url") val profilePhotoUrl: String, // Matches "profile_photo_url" in JSON
    //@SerializedName("profile_photo_path") val profilePhotoPath: String?,
)