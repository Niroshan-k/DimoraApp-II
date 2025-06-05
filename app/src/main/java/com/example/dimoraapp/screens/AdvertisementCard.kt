package com.example.dimoraapp.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.dimoraapp.model.Advertisement
import android.graphics.BitmapFactory
import java.net.URL

@Composable
fun AdvertisementCard(ad: Advertisement) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(ad.images.firstOrNull()?.data) {
        bitmap = ad.images.firstOrNull()?.data?.let {
            try {
                val url = URL(it)
                BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: Exception) {
                null
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
            Text(text = ad.title)
            ad.property_details?.let { property ->
                Text(text = "Price: ${property.price}")
                Text(text = "Location: ${property.location}")
            }
        }
    }
}