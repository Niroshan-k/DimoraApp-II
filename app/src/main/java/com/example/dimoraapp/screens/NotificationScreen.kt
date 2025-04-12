package com.example.dimoraapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dimoraapp.R
import com.example.dimoraapp.navigation.BottomNavBar

@Composable
fun NotificationScreen(navController: NavController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {

        // Content layout
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // LazyColumn for scrollable content
            Spacer(modifier = Modifier.height(64.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Take up remaining vertical space
                    .fillMaxWidth(),
            ) {
                item { Notification (onClick = {navController.navigate("infoscreen")}) }
                item { Notification (onClick = {navController.navigate("infoscreen")})}
                item { Notification (onClick = {navController.navigate("infoscreen")})}
                item { Notification (onClick = {navController.navigate("infoscreen")})}


            }

            // Bottom navigation bar
            BottomNavBar(navController = navController)
        }
    }
}

@Composable
fun Notification(onClick: () -> Unit) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(start = padding,8.dp, end = padding, bottom = 8.dp)
            .fillMaxWidth()
            .clickable{onClick()}
            .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = Color.Black,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.imageinfo1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(50.dp).height(70.dp).clip(shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "New Propert Listed ✌️. Check it out!",
                color = MaterialTheme.colorScheme.surface
            )
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ){
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "1d", color = MaterialTheme.colorScheme.surface
                    )
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "delete",
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(20.dp))
                }
            }

        }
    }
}