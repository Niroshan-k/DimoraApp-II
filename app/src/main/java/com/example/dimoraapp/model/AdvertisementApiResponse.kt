package com.example.dimoraapp.model

// For /api/advertisement/{id} endpoint
data class AdvertisementApiResponse(
    val data: AdvertisementApi
)

data class AdvertisementApi(
    val id: Int,
    val title: String?,
    val status: String?,
    val description: String?,
    val seller_id: Int?,
    val property: PropertyApi?,
    val images: List<ImageApi> = emptyList(),
    val created_at: String?,
    val updated_at: String?
)

data class PropertyApi(
    val id: Int,
    val location: String?,
    val price: String?, // backend sends string!
    val type: String?,
    val advertisement_id: Int?,
    val created_at: String?,
    val updated_at: String?,
    val house: HouseApi?
)

data class HouseApi(
    val id: Int,
    val property_id: Int?,
    val bedroom: String?,
    val bathroom: String?,
    val pool: Int?,
    val area: String?,
    val parking: Int?,
    val house_type: String?,
    val created_at: String?,
    val updated_at: String?
)

data class ImageApi(
    val id: Int,
    val data: String,
    val advertisement_id: Int?,
    val created_at: String,
    val updated_at: String
)