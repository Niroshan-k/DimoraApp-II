package com.example.dimoraapp.screens

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dimoraapp.ui.theme.DMserif
import com.example.dimoraapp.R
import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.data.repositor.AdvertisementRepository
import com.example.dimoraapp.model.Advertisement
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.utils.SessionManager
import com.example.dimoraapp.viewmodel.AdvertisementViewModel
import com.example.dimoraapp.viewmodel.AdvertisementViewModelFactory
import androidx.compose.ui.text.style.TextOverflow
import android.content.Intent
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current

    val sessionManager = remember { SessionManager(context) }
    val repository = remember { AdvertisementRepository(RetrofitClient.api, sessionManager) }
    val viewModel: AdvertisementViewModel = viewModel(
        factory = AdvertisementViewModelFactory(repository)
    )

    val ads by viewModel.ads
    val error by viewModel.error

    LaunchedEffect(ads) {
        println("LaunchedEffect triggered! ads.size = ${ads.size}")
        ads.forEach {
            println("Ad: ${it.title}, house: ${it.property_details?.house_details}")
        }
    }

    val luxuryAds = ads.filter { it.property_details?.house_details?.house_type == "luxury" }.take(4)
    val modernAds = ads.filter { it.property_details?.house_details?.house_type == "modern" }.take(4)
    val traditionalAds = ads.filter { it.property_details?.house_details?.house_type == "traditional" }.take(4)

    // Fetch ads when screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchAdvertisements()
    }

    BackHandler(enabled = true) {
        (context as? Activity)?.finish()
    }

    var isDrawerOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberLazyListState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = { TopNavBar(onMenuClick = { isDrawerOpen = true }, scrollBehavior = scrollBehavior) },
                bottomBar = { BottomNavBar(navController = navController) },
                content = { paddingValues ->
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    ) {
                        item{
                            Text(
                                text = "Discover Your",
                                fontFamily = DMserif,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                            )
                        }
                        item{
                            Text(
                                text = "New House!",
                                fontFamily = DMserif,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        item { Spacer(modifier = Modifier.padding(top = 16.dp)) }
                        item { Grid() }
                        item { Spacer(modifier = Modifier.padding(top = 16.dp)) }
                        item {
                            if (error != null) {
                                Text(text = error!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
                            } else {
                                Heading("Latest")
                            }
                        }
                        item {
                            AdvertisementsRow(ads = ads, navController = navController)
                        }
                        item { Spacer(modifier = Modifier.padding(top = 8.dp)) }
                        item { MoreButton(onClick = { navController.navigate("morehousescreen") }) }
                        item { Spacer(modifier = Modifier.padding(top = 16.dp)) }
                        item { Grid2() }
                        item { Spacer(modifier = Modifier.padding(top = 16.dp)) }
                        item { Heading("Luxury") }
                        item {
                            if (luxuryAds.isNotEmpty()) {
                                AdvertisementsRow(ads = luxuryAds, navController = navController)
                            }
                        }
                        item { Spacer(modifier = Modifier.padding(top = 8.dp)) }
                        item { MoreButton(onClick = { navController.navigate("morehousescreen") }) }
                        item { Spacer(modifier = Modifier.padding(top = 16.dp)) }
                        item { Heading("Modern") }
                        item {
                            if (modernAds.isNotEmpty()) {
                                AdvertisementsRow(ads = modernAds, navController = navController)
                            }
                        }
                        item { Spacer(modifier = Modifier.padding(top = 8.dp)) }
                        item { MoreButton(onClick = { navController.navigate("morehousescreen") }) }
                        item { Spacer(modifier = Modifier.padding(top = 16.dp)) }
                        item { Heading("Traditional") }
                        item {
                            if (traditionalAds.isNotEmpty()) {
                                AdvertisementsRow(ads = traditionalAds, navController = navController)
                            }
                        }
                        item { Spacer(modifier = Modifier.padding(top = 8.dp)) }
                        item { MoreButton(onClick = { navController.navigate("morehousescreen") }) }
                    }
                }
            )

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(onMenuClick: () -> Unit, scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = {},
        actions = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.background(Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More Options",
                    tint = MaterialTheme.colorScheme.surface,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior // Attach scroll behavior
    )
}

@Composable
fun Heading(title: String) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp
    Text(
        text = title,
        fontFamily = DMserif,
        fontSize = 35.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(start = padding)
    )
}

@Composable
fun Grid() {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryBox(
            imageRes = R.drawable.image1, // Replace with your image resource
            label = "Luxury",
            modifier = Modifier.weight(1f)
        )
        CategoryBox(
            imageRes = R.drawable.image2, // Replace with your image resource
            label = "Modern",
            modifier = Modifier.weight(1f)
        )
        CategoryBox(
            imageRes = R.drawable.image3, // Replace with your image resource
            label = "Traditional",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun Grid2() {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryBox2(
            imageRes = R.drawable.seller, // Replace with your image resource
            label = "Seller",
            label2 = "Become a verified Seller in Dimora",
            modifier = Modifier.weight(1f)
        )
        CategoryBox2(
            imageRes = R.drawable.contractor, // Replace with your image resource
            label = "Contractor",
            label2 = "Become a verified Contractor in Dimora",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CategoryBox(imageRes: Int, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}


@Composable
fun CategoryBox2(
    imageRes: Int,
    label: String,
    label2: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Optional: gradient overlay for better text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label2,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MoreButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(end = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "More", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdvertisementsRow(ads: List<Advertisement>, navController: NavController) {
    val context = LocalContext.current
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(ads) { ad ->
            AdvertisementCard(advertisement = ad, onShareClick = {
                val url = "https://dimoraland.onrender.com/advertisement/${ad.id}"
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, url)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Advertisement"))
            },onCardClick = { navController.navigate("infoscreen/${ad.id}") })
        }
    }
}

@Composable
fun AdvertisementCard(
    advertisement: Advertisement,
    onShareClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val imageUrl = advertisement.images.firstOrNull()?.data
    val house = advertisement.property_details?.house_details

    Card(
        modifier = Modifier
            .width(300.dp)
            .height(320.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image with gradient overlay
            if (!imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = advertisement.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                0.3f to Color.Transparent,
                                1.0f to Color.Black.copy(alpha = 0.7f)
                            )
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                )
            }

            // Status Chip (top right)
            val isActive = advertisement.status?.lowercase() == "active"
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Surface(
                    color = if (isActive) Color(0xFF43EA73) else Color(0xFFFF5252),
                    shape = RoundedCornerShape(30.dp),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = if (isActive) "Active" else "Sold",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 3.dp)
                    )
                }
            }

            // Card content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = advertisement.title ?: "No title",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DMserif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = advertisement.property_details?.location ?: "",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // House type or fallback
                Text(
                    text = if (house != null) "Type: ${house.house_type?.replaceFirstChar { it.uppercase() }}" else "No house details",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Price Chip
                    Surface(
                        color = Color(0xFF1DE9B6),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "Rs. ${advertisement.property_details?.price}",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {/* TODO: Implement favorite action */ },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SideNavBar(onClose: () -> Unit, onAboutUsClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopStart,
    ) {
        Column(modifier = Modifier.padding(start = padding).padding(top = 32.dp)) {
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAboutUsClick() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "About Us",
                    tint = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "About Us", color = MaterialTheme.colorScheme.surface, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_dark_mode_24),
                    contentDescription = "Dark Mode",
                    tint = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Dark Mode", color = MaterialTheme.colorScheme.surface, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Settings", color = MaterialTheme.colorScheme.surface, fontSize = 18.sp)
            }
        }
    }
}
