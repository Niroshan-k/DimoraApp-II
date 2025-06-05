package com.example.dimoraapp.model

data class AdvertisementListResponse(
    val data: List<Advertisement>
)

data class Advertisement(
    val id: Int,
    val title: String,
    val property_details: PropertyDetails,
    val images: List<Image>
)

data class PropertyDetails(
    val location: String,
    val price: Double
)

data class Image(
    val data: String
)