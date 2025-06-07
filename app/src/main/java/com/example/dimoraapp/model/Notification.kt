package com.example.dimoraapp.model

data class Notification(
    val _id: IdObject?,
    val advertisement_id: Int?,
    val seller_id: Int?,
    val seller_name: String?,
    val message: String?,
    val created_at: String?
)

data class IdObject(
    val `$oid`: String?
)