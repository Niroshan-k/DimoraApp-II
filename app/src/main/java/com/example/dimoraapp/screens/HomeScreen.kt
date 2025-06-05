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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dimoraapp.ui.theme.DMserif
import com.example.dimoraapp.R
import com.example.dimoraapp.model.Picture
import com.example.dimoraapp.data.Datasource
import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.data.repositor.AdvertisementRepository
import com.example.dimoraapp.model.Advertisement
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.utils.SessionManager
import com.example.dimoraapp.viewmodel.AdvertisementViewModel
import com.example.dimoraapp.viewmodel.AdvertisementViewModelFactory
import androidx.compose.foundation.lazy.items


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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { Heading("Latest Houses") }
                        item { PicturesApp(navController) }
                        item { MoreButton(onClick = { navController.navigate("morehousescreen") }) }
                        item { Heading("Luxury Houses") }

                        item { MoreButton(onClick = { navController.navigate("morehousescreen") }) }
                        item { Heading("Modern Houses") }

                        item { MoreButton(onClick = { navController.navigate("morehousescreen") }) }
                        item { Heading("Traditional Houses") }

                        item { MoreButton(onClick = { navController.navigate("morehousescreen") }) }
                        item {
                            if (error != null) {
                                Text(text = error!!, color = Color.Red)
                            } else {
                                AdvertisementListScreen(advertisements = ads)
                            }
                        }
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
        fontSize = 40.sp,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(start = padding)
    )
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
fun PictureCard(picture: Picture, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .width(250.dp)
            .height(250.dp)
            .padding(start = 8.dp)
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = picture.drawableResourseId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(picture.name),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DMserif
                )
                Text(
                    text = stringResource(picture.price),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DMserif
                )
            }
        }
    }
}

@Composable
fun PicturesApp(navController: NavController) {
    PictureList(
        pictureList = Datasource().loadPictures(),
        navController = navController
    )
}

@Composable
fun PictureList(pictureList: List<Picture>, navController: NavController, modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 0.dp
    LazyRow(
        modifier = modifier.fillMaxWidth().padding(start = padding),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pictureList.size) { index ->
            PictureCard(
                picture = pictureList[index],
                onClick = {
                    navController.navigate("infoscreen")
                }
            )
        }
        item {
            Box(
                modifier = Modifier
                    .height(250.dp)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { /* Handle next page action */ },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = CircleShape
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Next Page",
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
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
            .width(250.dp) // Adjust width as needed
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopStart, // Adjust content alignment inside the navbar
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



@Composable
fun AdvertisementListScreen(advertisements: List<Advertisement>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Advertisements",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(advertisements) { ad ->
            AdvertisementItem(advertisement = ad)
        }
    }
}

@Composable
fun AdvertisementItem(advertisement: Advertisement) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = advertisement.title, style = MaterialTheme.typography.titleLarge)
        Text(text = advertisement.property_details.location)
        Text(text = "Price: ${advertisement.property_details.price}")
    }
}