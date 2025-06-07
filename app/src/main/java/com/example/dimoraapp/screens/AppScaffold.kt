package com.example.dimoraapp.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.dimoraapp.navigation.BottomNavBar

@Composable
fun AppScaffold(
    navController: NavHostController,
    notificationCount: Int,
    onNotificationsClicked: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                notificationCount = notificationCount,
                onNotificationsClicked = onNotificationsClicked
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
