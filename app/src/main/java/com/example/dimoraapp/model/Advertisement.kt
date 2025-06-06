package com.example.dimoraapp.model

data class AdvertisementListResponse(
    val data: List<Advertisement>
)

data class Advertisement(
    val id: Int,
    val title: String?,
    val status: String?,
    val description: String?,
    val seller_id: Int?,
    val property_details: PropertyDetails?,
    val images: List<Image> = emptyList(),
    val created_at: String?,
    val updated_at: String?
)

data class PropertyDetails(
    val id: Int,
    val location: String?,
    val price: Double?,
    val type: String?,
    val house_details: House?
)

data class House(
    val id: Int,
    val bedrooms: String?,
    val bathrooms: String?,
    val parking: Int?,
    val pool: Int?,
    val area: String?,
    val property_id: Int?,
    val house_type: String?
)
data class Image(
    val id: Int,
    val data: String,
    val created_at: String,
    val updated_at: String
)