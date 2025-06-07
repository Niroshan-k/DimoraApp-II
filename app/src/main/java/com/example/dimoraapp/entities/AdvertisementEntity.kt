package com.example.dimoraapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "advertisements")
data class AdvertisementEntity(
    @PrimaryKey val id: Int,
    val title: String?,
    val status: String?,
    val description: String?,
    val sellerId: Int?,
    val location: String?,
    val price: Double?,
    val type: String?,
    val houseType: String?,
    val imageUrl: String?,        // We'll save the first image's URL
    val createdAt: String?,
    val updatedAt: String?
)