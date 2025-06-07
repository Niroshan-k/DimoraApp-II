package com.example.dimoraapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun saveProfileImageToInternalStorage(context: Context, bitmap: Bitmap): String {
    // Remove existing profile image
    val dir = context.filesDir
    val oldFile = File(dir, "profile_picture.jpg")
    if (oldFile.exists()) oldFile.delete()
    // Save new
    val file = File(context.filesDir, "profile_picture.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file.absolutePath
}

fun getSavedProfileImagePath(context: Context): String? {
    val file = File(context.filesDir, "profile_picture.jpg")
    return if (file.exists()) file.absolutePath else null
}

fun removeProfileImage(context: Context) {
    val file = File(context.filesDir, "profile_picture.jpg")
    if (file.exists()) file.delete()
}