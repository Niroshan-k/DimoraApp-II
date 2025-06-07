package com.example.dimoraapp.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dimoraapp.R
import com.example.dimoraapp.viewmodel.NotificationViewModel

@Composable
fun BottomNavBar(
    navController: NavController,
    notificationCount: Int,
    onNotificationsClicked: () -> Unit
){
    var selectedTab by remember { mutableStateOf("") }

    NavigationBar(
        modifier = Modifier
            .shadow(3.dp, shape = RectangleShape),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        NavigationBarItem(
            icon = {
                Icon(Icons.Filled.Home, contentDescription = "Home",
                    tint = if (selectedTab == "home") MaterialTheme.colorScheme.scrim
                    else MaterialTheme.colorScheme.surface
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.home),
                    fontWeight = FontWeight.Bold)
            },
            selected = false,
            onClick = {
                navController.navigate("homescreen")
                selectedTab = "home"
            }
        )

        NavigationBarItem(
            icon = {
                Icon(Icons.Filled.Search, contentDescription = "Search",
                    tint = if (selectedTab == "search") MaterialTheme.colorScheme.scrim
                    else MaterialTheme.colorScheme.surface)
            },
            label = {
                Text(
                    text = stringResource(R.string.search),
                    fontWeight = FontWeight.Bold)
            },
            selected = false,
            onClick = { navController.navigate("searchscreen")
                selectedTab = "search"
            }
        )

        NavigationBarItem(
            icon = {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge(containerColor = Color.Red) {
                                Text(text = notificationCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        tint = if (selectedTab == "notification") MaterialTheme.colorScheme.scrim
                        else MaterialTheme.colorScheme.surface
                    )
                }
            },
            label = {
                Text(
                    text = stringResource(R.string.notifications),
                    fontWeight = FontWeight.Bold
                )
            },
            selected = false,
            onClick = {
                onNotificationsClicked()
                selectedTab = "notification"
            }
        )

        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(Color.LightGray, CircleShape), // Background circle
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape) // Ensures the image is circular
                    )
                }
            },
            label = {
                Text(
                    text = stringResource(R.string.profile),
                    fontWeight = FontWeight.Bold)
            },
            selected = false,
            onClick = { navController.navigate("profilescreen") }
        )
    }
}