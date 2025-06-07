package com.example.dimoraapp.mappers

import com.example.dimoraapp.entities.AdvertisementEntity
import com.example.dimoraapp.model.Advertisement

fun Advertisement.toEntity(): AdvertisementEntity = AdvertisementEntity(
    id = id,
    title = title,
    status = status,
    description = description,
    sellerId = seller_id,
    location = property_details?.location,
    price = property_details?.price,
    type = property_details?.type,
    houseType = property_details?.house_details?.house_type,
    imageUrl = images.firstOrNull()?.data,
    createdAt = created_at,
    updatedAt = updated_at
)