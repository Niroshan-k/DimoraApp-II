package com.example.dimoraapp.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.dimoraapp.R
import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.data.repositor.AdvertisementRepository
import com.example.dimoraapp.model.AdvertisementApi
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.utils.SessionManager
import com.example.dimoraapp.viewmodel.SearchViewModel
import com.example.dimoraapp.viewmodel.SearchViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun SearchScreen(
    navController: NavController,
    notificationCount: Int,
    onNotificationsClicked: () -> Unit,
) {
    val context = LocalContext.current
    val profileImagePath = getSavedProfileImagePath(context)
    var query by remember { mutableStateOf("") }
    val sessionManager = remember { SessionManager(context) }
    val api = remember { RetrofitClient.api }
    val repository = remember { AdvertisementRepository(api, sessionManager) }
    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(repository)
    )
    var isDrawerOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopNavBar(onMenuClick = { isDrawerOpen = true }, scrollBehavior = scrollBehavior)
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { if (query.isNotBlank()) viewModel.search(query) }
            )
            // Results area
            Box(modifier = Modifier.weight(1f)) {
                when {
                    viewModel.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    viewModel.error != null -> {
                        ErrorMessage(
                            errorMessage = viewModel.error ?: "Unknown error",
                            imageRes = R.drawable.ic_error_image,
                            title = "404 Error"
                        )
                    }
                    viewModel.results.isEmpty() && query.isNotBlank() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No results found", fontSize = 18.sp)
                        }
                    }
                    viewModel.results.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(viewModel.results) { ad ->
                                SearchResultCard(ad) {
                                    navController.navigate("infoscreen/${ad}")
                                }
                            }
                        }
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
    }
}

@Composable
fun ErrorMessage(
    errorMessage: String,
    imageRes: Int,
    title: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Error Image",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )
            title?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = padding, end = padding, top = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val width = if (isLandscape) 650.dp else 250.dp
        TextField(
            modifier = Modifier.width(width),
            value = query,
            onValueChange = { onQueryChange(it) },
            placeholder = { Text("Search title or location") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon"
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.surface,
                unfocusedTextColor = MaterialTheme.colorScheme.surface
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        val buttonWidth = if (isLandscape) 250.dp else 150.dp
        Button(
            modifier = Modifier
                .height(55.dp)
                .width(buttonWidth),
            onClick = onSearch,
            enabled = query.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Search",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SearchResultCard(
    ad: AdvertisementApi,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            val imageUrl = ad.images?.firstOrNull()?.data
            if (!imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = ad.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(ad.title ?: "No title", fontWeight = FontWeight.Bold, maxLines = 1)
                Text(ad.property?.location ?: "", color = Color.Gray, maxLines = 1)
                Text(
                    "Rs. ${ad.property?.price ?: ""}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                // Optionally, show house details
                ad.property?.house?.let { house ->
                    Text("Type: ${house.house_type}", fontSize = 12.sp)
                }
            }
        }
    }
}