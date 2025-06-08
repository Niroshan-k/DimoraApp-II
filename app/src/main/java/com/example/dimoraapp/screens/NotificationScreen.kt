package com.example.dimoraapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.data.repositor.NotificationRepository
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.viewmodel.NotificationViewModel
import com.example.dimoraapp.viewmodel.NotificationViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    token: String,
    navController: NavHostController,
    notificationViewModel: NotificationViewModel,
    notificationCount: Int,
    onNotificationsClicked: () -> Unit
) {
    val repository = remember { NotificationRepository(RetrofitClient.api) }
    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(repository, token) as androidx.lifecycle.ViewModelProvider.Factory
    )
    val notifications by viewModel.notifications.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val profileImagePath = getSavedProfileImagePath(context)
    val serverError = rememberServerErrorMessage()
    LaunchedEffect(Unit) {
        notificationViewModel.clearNotificationCount()
    }

    Scaffold(
        topBar = {
            TopNavBarInfo(
                goToHomePage = { navController.navigate("homescreen") },
                onMenuClick = { /* implement menu click if needed */ }
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                notificationCount = notificationCount,
                onNotificationsClicked = onNotificationsClicked,
                profileImagePath = profileImagePath
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            when {

                loading -> CircularProgressIndicator()
                error != null ->
                    if (error != null) {
                        if (serverError != null) {
                            ErrorScreenWithImage(serverError)
                        }
                    }
                notifications.isEmpty() -> Text(
                    "No notifications found or failed to load notifications.",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(32.dp)
                )
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationItem(notification)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: com.example.dimoraapp.model.Notification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape = MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            notification.seller_name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,

                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            notification.message?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,

                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            notification.created_at?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}