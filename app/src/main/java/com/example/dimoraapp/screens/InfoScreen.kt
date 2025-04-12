package com.example.dimoraapp.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.dimoraapp.R
import com.example.dimoraapp.data.Datasource
import com.example.dimoraapp.model.PictureInfo
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.ui.theme.DMserif
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun InfoScreen(navController: NavController){
    var isDrawerOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {

        // Content layout
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // LazyColumn for scrollable content
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Take up remaining vertical space
                    .fillMaxWidth(),
            ) {
                item {
                    TopNavBarInfo(
                        goToHomePage = { navController.navigate("homescreen")},
                        onMenuClick = { isDrawerOpen = true })
                }
                item { PicturesList() }
                item { HouseDetails() }

            }

            // Bottom navigation bar
            BottomNavBar(navController = navController)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBarInfo(goToHomePage: () -> Unit, onMenuClick: () -> Unit) {
    TopAppBar(
        title = {Text(text = "Home", color = MaterialTheme.colorScheme.surface, fontSize = 18.sp)},
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
            IconButton(
                onClick = onMenuClick,
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More Options",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            actionIconContentColor = Color.Black,
            navigationIconContentColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = Color.Black
        )
    )
}

@Composable
fun BannerImage(selectedImageResId: Int) {
    Image(
        painter = painterResource(selectedImageResId),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .height(300.dp)
            .clip(shape = RoundedCornerShape(8.dp))
    )
}
@Composable
fun PictureCardInfo(
    picture: PictureInfo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .width(90.dp)
            .height(90.dp)
            .padding(start = 8.dp)
            .clickable { onClick() }, // Trigger the onClick action
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = picture.drawableResourseId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun PicturesList() {
    // Use a state to track the selected image resource ID
    val pictureList = Datasource().loadPicturesInfo()
    val (selectedImageResId, setSelectedImageResId) = remember {
        mutableStateOf(pictureList.first().drawableResourseId) // Default to the first picture
    }

    Column {
        // Banner Image displaying the selected image
        BannerImage(selectedImageResId)

        // Picture List
        PictureListInfo(
            pictureList = pictureList,
            onPictureClick = { selectedPicture ->
                setSelectedImageResId(selectedPicture.drawableResourseId)
            }
        )
    }
}

@Composable
fun PictureListInfo(
    pictureList: Array<PictureInfo>,
    modifier: Modifier = Modifier,
    onPictureClick: (PictureInfo) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(start = padding),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(pictureList.size) { index ->
            PictureCardInfo(
                picture = pictureList[index],
                onClick = {
                    onPictureClick(pictureList[index]) // Pass the clicked picture to the callback
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseDetails() {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val padding = if (isLandscape) 64.dp else 16.dp

    Column {
        Heading(stringResource(R.string.price1))
        Box(){
            Row(
                modifier = Modifier.padding(start = padding)
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = "location", tint = MaterialTheme.colorScheme.surface)
                Text(
                    text = stringResource(R.string.Heading1),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
        Text(
            text = stringResource(R.string.property_details),
            modifier = Modifier
                .padding(top = 30.dp, start = padding),
            color = Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        //first row
        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .width(100.dp)
                        .shadow(2.dp, shape = RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                        ) {
                            Icon(painter = painterResource(R.drawable.baseline_bed_24), contentDescription = "bedroom", tint = MaterialTheme.colorScheme.surface)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "2",fontWeight = FontWeight.Medium,color = MaterialTheme.colorScheme.surface)
                        }
                        Text(
                            text = "Bedroom",
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.surface)
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .width(100.dp)
                        .shadow(2.dp, shape = RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                        ) {
                            Icon(painter = painterResource(R.drawable.baseline_bathtub_24), contentDescription = "bedroom",tint = MaterialTheme.colorScheme.surface)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "1",fontWeight = FontWeight.Medium,color = MaterialTheme.colorScheme.surface)
                        }
                        Text(
                            text = "Bathroom",
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.surface)
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .width(100.dp)
                        .shadow(2.dp, shape = RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                        ) {
                            Icon(painter = painterResource(R.drawable.baseline_square_foot_24), contentDescription = "bedroom",tint = MaterialTheme.colorScheme.surface)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "45",fontWeight = FontWeight.Medium,color = MaterialTheme.colorScheme.surface)
                        }
                        Text(
                            text = "sqft",
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.surface)
                    }
                }
            }
        }
        //second row
        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .width(100.dp)
                        .shadow(2.dp, shape = RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                        ) {
                            Icon(Icons.Filled.DateRange, contentDescription = "bedroom",tint = MaterialTheme.colorScheme.surface)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "2020",fontWeight = FontWeight.Medium,color = MaterialTheme.colorScheme.surface)
                        }
                        Text(
                            text = "Year",
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.surface)
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .width(100.dp)
                        .shadow(2.dp, shape = RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                        ) {
                            Icon(painter = painterResource(R.drawable.baseline_directions_car_24), contentDescription = "bedroom",tint = MaterialTheme.colorScheme.surface)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "1",fontWeight = FontWeight.Medium,color = MaterialTheme.colorScheme.surface)
                        }
                        Text(
                            text = "Car Park",
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.surface)
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .width(100.dp)
                        .shadow(2.dp, shape = RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                        ) {
                            Icon(painter = painterResource(R.drawable.baseline_pool_24), contentDescription = "bedroom",tint = MaterialTheme.colorScheme.surface)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "1",fontWeight = FontWeight.Medium,color = MaterialTheme.colorScheme.surface)
                        }
                        Text(
                            text = "Pool",
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.surface)
                    }
                }
            }
        }
        Text(
            text = stringResource(R.string.location),
            modifier = Modifier
                .padding(top = 30.dp, start = padding),
            color = Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.description),
            modifier = Modifier
                .padding(top = 30.dp, start = padding),
            color = Color.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.description_long),
            modifier = Modifier
                .padding(top = 24.dp, start = padding, end = padding),
            color = MaterialTheme.colorScheme.surface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = "Connect With the Seller",
            modifier = Modifier
                .padding(top = 32.dp, start = padding, end = padding),
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 35.sp,
            fontFamily = DMserif

        )
        Form()
        //GoogleMapScreen()
    }
}
@Composable
fun GoogleMapScreen() {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    LaunchedEffect(mapView) {
        mapView.onCreate(null)
        mapView.onResume()
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
fun MapScreen() {
    Box(modifier = Modifier) {
        val singapore = LatLng(1.3521, 103.8198) // Example: Singapore location
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 12f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = rememberMarkerState(position = singapore),
                title = "Singapore",
                snippet = "A beautiful place!"
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form() {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val email = remember { mutableStateOf("example@gmail.com") }
    val contact = remember { mutableStateOf("") }

    val padding = if (isLandscape) 64.dp else 24.dp

    Column(
        modifier = Modifier
            .padding(top = 24.dp, start = padding, end = padding)
    ) {
        // Define a list of pairs (label, state)
        val formFields = listOf(
            "Email" to email,
            "Contact" to contact,
        )

        // Loop through the list and create a TextField for each pair
        formFields.forEach { (label, state) ->
            TextField(
                value = state.value,
                onValueChange = { state.value = it },
                label = { Text(label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(start = padding, end = padding, bottom = 16.dp)
                    .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
        var message by remember { mutableStateOf("") }

        TextField(
            value = message,
            onValueChange = {message = it},
            label = { Text("Message") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(start = padding, end = padding, bottom = 16.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = padding, end = padding)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium),
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Send Message", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


