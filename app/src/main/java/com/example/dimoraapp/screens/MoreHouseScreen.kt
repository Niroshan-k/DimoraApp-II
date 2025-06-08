package com.example.dimoraapp.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dimoraapp.R
import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.data.repositor.AdvertisementRepository
import com.example.dimoraapp.model.Advertisement
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.ui.theme.DMserif
import com.example.dimoraapp.utils.SessionManager
import com.example.dimoraapp.viewmodel.AdvertisementViewModel
import com.example.dimoraapp.viewmodel.AdvertisementViewModelFactory

@Composable
fun MoreHouseScreen(
    navController: NavController,
    notificationCount: Int,
    onNotificationsClicked: () -> Unit,
    category: String // "latest", "luxury", "modern", "traditional"
) {
    var isDrawerOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val profileImagePath = getSavedProfileImagePath(context)

    val sessionManager = remember { SessionManager(context) }
    val repository = remember { AdvertisementRepository(RetrofitClient.api, sessionManager) }
    val viewModel: AdvertisementViewModel = viewModel(
        factory = AdvertisementViewModelFactory(repository)
    )
    val ads by viewModel.ads

    // Fetch ads when screen loads
    LaunchedEffect(Unit) { viewModel.fetchAdvertisements() }

    val filteredAds = when (category.lowercase()) {
        "luxury" -> ads.filter { it.property_details?.house_details?.house_type == "luxury" }
        "modern" -> ads.filter { it.property_details?.house_details?.house_type == "modern" }
        "traditional" -> ads.filter { it.property_details?.house_details?.house_type == "traditional" }
        // "latest" could be all ads sorted by date if you have a date property. Here, just show all.
        else -> ads
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Navigation Bar
            TopNavBarInfo(
                goToHomePage = { navController.navigate("homescreen") },
                onMenuClick = { isDrawerOpen = true }
            )

            // Title
            Text(
                text = when (category.lowercase()) {
                    "luxury" -> "Luxury Houses"
                    "modern" -> "Modern Houses"
                    "traditional" -> "Traditional Houses"
                    else -> "Latest Houses"
                },
                fontFamily = DMserif,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(16.dp)
            )

            // LazyColumn for filtered ads
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                items(filteredAds.size) { idx ->
                    val ad = filteredAds[idx]
                    SmallAdvertisementCard(advertisement = ad) {
                        navController.navigate("infoscreen/${ad.id}")
                    }
                }
                if (filteredAds.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No advertisements found for this category.",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Bottom navigation bar
            BottomNavBar(
                navController = navController,
                notificationCount = notificationCount,
                onNotificationsClicked = onNotificationsClicked,
                profileImagePath = profileImagePath
            )
        }

        // Side drawer for navigation
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInHorizontally(initialOffsetX = { -300 }),
            exit = slideOutHorizontally(targetOffsetX = { -300 })
        ) {
            SideNavBar(
                onClose = { isDrawerOpen = false },
                onAboutUsClick = { navController.navigate("about_us") }
            )
        }
    }
}

@Composable
fun SmallAdvertisementCard(
    advertisement: Advertisement,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            val imageUrl = advertisement.images.firstOrNull()?.data
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = advertisement.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = advertisement.title ?: "No title",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Rs. ${advertisement.property_details?.price ?: "-"}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = advertisement.property_details?.location ?: "-",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}