package com.example.dimoraapp.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.net.Uri
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
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
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InfoScreen(
    navController: NavController,
    adId: Int,
    notificationCount: Int,
    onNotificationsClicked: () -> Unit
) {
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

    // Permission handling
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    // Early return if no permission
    if (!permissionState.status.isGranted) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Location permission not granted")
        }
        return
    }

    // Now it's safe to use location in your UI
    var isDrawerOpen by remember { mutableStateOf(false) }
    val profileImagePath = getSavedProfileImagePath(context)

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
                        // Show Route Map if property location available
                        item {
                            val address = ad!!.property?.location
                            if (!address.isNullOrBlank()) {
                                Text(
                                    text = "Show Route to Property",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                                )
                                RouteMapScreen(propertyAddress = address)
                            }
                        }
                        item { SendMessageForm() }
                    }
                }
            }
            BottomNavBar(
                navController = navController,
                notificationCount = notificationCount,
                onNotificationsClicked = onNotificationsClicked,
                profileImagePath = profileImagePath
            )
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
        // Property location map
        property?.location?.let { locationString ->
            if (locationString.isNotBlank()) {
                AddressToMapView(address = locationString)
            }
        }
    }
}

@Composable
fun AddressToMapView(address: String) {
    val context = LocalContext.current
    var latLon by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    LaunchedEffect(address) {
        latLon = geocodeAddress(context, address)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        latLon?.let { (lat, lon) ->
            OpenStreetMapView(
                lat = lat,
                lon = lon,
                modifier = Modifier.matchParentSize()
            )
        } ?: Text("Loading map...")
    }
}

@Composable
fun OpenStreetMapView(lat: Double, lon: Double, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(lat, lon))
                setMultiTouchControls(true)

                // Add marker at property location
                val marker = Marker(this).apply {
                    position = GeoPoint(lat, lon)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Property"
                }
                overlays.add(marker)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            val marker = Marker(mapView).apply {
                position = GeoPoint(lat, lon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Property"
            }
            mapView.overlays.add(marker)
            mapView.controller.setCenter(GeoPoint(lat, lon))
            mapView.invalidate()
        }
    )
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
    val context = LocalContext.current

    fun openSmsApp() {
        val smsNumber = "+94788672025"
        val smsBody = "Email: $email\nContact: $contact\nMessage: $message"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$smsNumber")
            putExtra("sms_body", smsBody)
        }
        context.startActivity(intent)
    }

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
                .height(80.dp)
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
                .height(80.dp)
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
                .height(110.dp)
                .padding(bottom = 16.dp),
            shape = MaterialTheme.shapes.medium,
            maxLines = 5
        )
        Button(
            onClick = { openSmsApp() },
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

// --- MAP/ROUTE LOGIC ---

@Composable
fun rememberCurrentLocation(context: Context): Location? {
    var location by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(true) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
                location = loc
            }
        }
    }
    return location
}

fun geocodeAddress(context: Context, address: String): Pair<Double, Double>? {
    return try {
        val geocoder = Geocoder(context)
        val results = geocoder.getFromLocationName(address, 1)
        if (results.isNullOrEmpty()) {
            Log.e("geocodeAddress", "No results for address: $address, using fallback")
            // Fallback: return fixed coordinates for testing
            Pair(7.287467, 80.640764) // Googleplex
        } else {
            val lat = results[0].latitude
            val lon = results[0].longitude
            Pair(lat, lon)
        }
    } catch (e: Exception) {
        Log.e("geocodeAddress", "Exception: ${e.localizedMessage}, using fallback")
        // Fallback: return fixed coordinates for testing
        Pair(37.4219983, -122.084)
    }
}
suspend fun getRoutePoints(
    startLat: Double, startLon: Double,
    endLat: Double, endLon: Double
): List<GeoPoint> {
    return withContext(Dispatchers.IO) {
        val url = "https://router.project-osrm.org/route/v1/driving/$startLon,$startLat;$endLon,$endLat?overview=full&geometries=geojson"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val json = JSONObject(response.body?.string() ?: "")
        val points = mutableListOf<GeoPoint>()
        val coords = json
            .getJSONArray("routes")
            .getJSONObject(0)
            .getJSONObject("geometry")
            .getJSONArray("coordinates")
        for (i in 0 until coords.length()) {
            val lng = coords.getJSONArray(i).getDouble(0)
            val lat = coords.getJSONArray(i).getDouble(1)
            points.add(GeoPoint(lat, lng))
        }
        points
    }
}

@Composable
fun RouteMapScreen(
    propertyAddress: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userLocation = rememberCurrentLocation(context)
    var destLatLon by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Geocode the property address
    LaunchedEffect(propertyAddress) {
        loading = true
        errorMsg = null
        destLatLon = geocodeAddress(context, propertyAddress)
        loading = false
        if (destLatLon == null) errorMsg = "Could not geocode property address"
    }

    // Get route points when both locations are ready
    LaunchedEffect(userLocation, destLatLon) {
        if (userLocation != null && destLatLon != null) {
            loading = true
            errorMsg = null
            try {
                routePoints = getRoutePoints(
                    userLocation.latitude, userLocation.longitude,
                    destLatLon!!.first, destLatLon!!.second
                )
            } catch (e: Exception) {
                errorMsg = "Error getting route: ${e.localizedMessage}"
            }
            loading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(16.dp) // Add padding
            .clip(RoundedCornerShape(16.dp)) // Rounded corners
            .background(Color.LightGray), // Background color
        contentAlignment = Alignment.Center
    ) {
        when {
            loading -> CircularProgressIndicator()
            errorMsg != null -> Text(errorMsg ?: "", color = Color.Red)
            userLocation == null -> Text("Getting current location…")
            destLatLon == null -> Text("Getting property location…")
            else -> {
                val start = GeoPoint(userLocation.latitude, userLocation.longitude)
                val end = GeoPoint(destLatLon!!.first, destLatLon!!.second)
                AndroidView(
                    modifier = Modifier.matchParentSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                            controller.setZoom(14.5)
                            controller.setCenter(start)
                            setMultiTouchControls(true)
                        }
                    },
                    update = { mapView ->
                        mapView.overlays.clear()

                        // Draw the route as a polyline
                        if (routePoints.isNotEmpty()) {
                            val polyline = Polyline(mapView).apply {
                                setPoints(routePoints)
                                outlinePaint.color = android.graphics.Color.BLUE
                                outlinePaint.strokeWidth = 10f
                            }
                            mapView.overlays.add(polyline)
                        }

                        // Add start marker
                        Marker(mapView).apply {
                            position = start
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "You"
                            mapView.overlays.add(this)
                        }
                        // Add end marker
                        Marker(mapView).apply {
                            position = end
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Property"
                            mapView.overlays.add(this)
                        }

                        // Center map
                        mapView.controller.setCenter(start)
                        mapView.invalidate()
                    }
                )
            }
        }
    }
}