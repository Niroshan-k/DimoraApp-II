package com.example.dimoraapp.screens

import android.content.Context
import android.content.res.Configuration
import android.location.Geocoder
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dimoraapp.R
import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.data.repositor.AdvertisementRepository
import com.example.dimoraapp.model.*
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.utils.SessionManager
import com.example.dimoraapp.viewmodel.InfoViewModel
import com.example.dimoraapp.viewmodel.InfoViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dimoraapp.ui.theme.DMserif

@Composable
fun InfoScreen(navController: NavController, adId: Int) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val repository = remember { AdvertisementRepository(RetrofitClient.api, sessionManager) }
    val viewModel: InfoViewModel = viewModel(
        factory = InfoViewModelFactory(repository, sessionManager)
    )

    val ad by viewModel.ad
    val error by viewModel.error

    // Fetch ad on first load
    LaunchedEffect(adId) { viewModel.fetchAdvertisementById(adId) }

    // Debug: log ad
    LaunchedEffect(ad) { Log.d("InfoScreen", "Loaded ad: $ad") }

    var isDrawerOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                item {
                    TopNavBarInfo(
                        goToHomePage = { navController.navigate("homescreen") },
                        onMenuClick = { isDrawerOpen = true }
                    )
                }
                when {
                    error != null -> item {
                        Text(
                            "Error: $error",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    ad == null -> item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        item { PicturesCarousel(images = ad!!.images) }
                        item { HouseDetailsSection(ad = ad!!) }
                        item {
                            Divider(
                                color = Color.LightGray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)
                            )
                        }
                        item { SendMessageForm() }
                    }
                }
            }
            BottomNavBar(navController = navController)
        }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBarInfo(goToHomePage: () -> Unit, onMenuClick: () -> Unit) {
    TopAppBar(
        title = { Text("Home", color = MaterialTheme.colorScheme.surface, fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = goToHomePage) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Homepage",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More Options",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = Color.Black,
            navigationIconContentColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = Color.Black
        )
    )
}

@Composable
fun PicturesCarousel(images: List<ImageApi>) {
    val (selectedImageUrl, setSelectedImageUrl) = remember {
        mutableStateOf(images.firstOrNull()?.data)
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        ) {
            selectedImageUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (images.size > 1) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(images.size) { index ->
                    val img = images[index]
                    AsyncImage(
                        model = img.data,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { setSelectedImageUrl(img.data) }
                    )
                }
            }
        }
    }
}

@Composable
fun HouseDetailsSection(ad: AdvertisementApi) {
    val property = ad.property
    val house = property?.house
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp

    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        // Price and status
        Row(
            modifier = Modifier.padding(start = padding, top = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rs. ${property?.price ?: "-"}",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            val isActive = ad.status?.lowercase() == "active"
            Box(Modifier.padding(start = 8.dp)) {
                Surface(
                    color = if (isActive) Color(0xFF43EA73) else Color(0xFFFF5252),
                    shape = RoundedCornerShape(30.dp),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = if (isActive) "Active" else "Sold",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 3.dp)
                    )
                }
            }
        }
        // Title
        Text(
            text = ad.title ?: "-",
            fontWeight = FontWeight.Bold,
            fontFamily = DMserif,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = padding, top = 8.dp)
        )
        // Location
        Row(
            modifier = Modifier.padding(start = padding, top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = "location", tint = MaterialTheme.colorScheme.surface)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = property?.location ?: "-",
                color = MaterialTheme.colorScheme.surface,
                fontWeight = FontWeight.Medium
            )
        }
        // Property details
        Text(
            text = "Property Details",
            modifier = Modifier.padding(top = 24.dp, start = padding),
            color = Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoBox(icon = R.drawable.baseline_bed_24, label = "Bedroom", value = house?.bedroom ?: "-")
            InfoBox(icon = R.drawable.baseline_bathtub_24, label = "Bathroom", value = house?.bathroom ?: "-")
            InfoBox(icon = R.drawable.baseline_square_foot_24, label = "Area", value = house?.area ?: "-")
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoBox(icon = R.drawable.baseline_directions_car_24, label = "Car Park", value = house?.parking?.toString() ?: "-")
            InfoBox(icon = R.drawable.baseline_pool_24, label = "Pool", value = house?.pool?.toString() ?: "-")
            InfoBox(icon = R.drawable.baseline_bed_24, label = "Type", value = house?.house_type ?: "-")
        }
        // Description
        Text(
            text = "Description",
            modifier = Modifier.padding(top = 24.dp, start = padding),
            color = Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = ad.description ?: "-",
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.surface,
            fontSize = 15.sp
        )
        property?.location?.let { locationString ->
            if (locationString.isNotBlank()) {
                AddressToMapView(address = locationString)
            }
        }
    }
}

fun geocodeAddress(context: Context, address: String): Pair<Double, Double>? {
    return try {
        val geocoder = Geocoder(context)
        val results = geocoder.getFromLocationName(address, 1)
        if (results.isNullOrEmpty()) null
        else Pair(results[0].latitude, results[0].longitude)
    } catch (e: Exception) {
        null
    }
}
@Composable
fun AddressToMapView(address: String) {
    val context = LocalContext.current
    var latLon by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    LaunchedEffect(address) {
        latLon = geocodeAddress(context, address)
    }

    latLon?.let { (lat, lon) ->
        OpenStreetMapView(
            lat = lat,
            lon = lon,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Make the map bigger
                .padding(16.dp)
        )
    } ?: Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading map...")
    }
}

@Composable
fun InfoBox(icon: Int, label: String, value: String) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .height(90.dp)
            .width(90.dp)
            .shadow(2.dp, shape = RoundedCornerShape(4.dp))
            .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                tint = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.surface)
            Text(
                text = label,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMessageForm() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var email by remember { mutableStateOf("example@gmail.com") }
    var contact by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val padding = if (isLandscape) 64.dp else 24.dp

    Column(
        modifier = Modifier
            .padding(top = 16.dp, start = padding, end = padding, bottom = 32.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Text(
            "Send Message",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 12.dp),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )
        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text("Contact") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 12.dp),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(bottom = 16.dp),
            shape = MaterialTheme.shapes.medium,
            maxLines = 5
        )
        Button(
            onClick = { /* TODO: Send message logic here */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Send Message", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}