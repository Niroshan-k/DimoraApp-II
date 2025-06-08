package com.example.dimoraapp.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.data.repositor.NotificationRepository
import com.example.dimoraapp.screens.GetStartedScreen
import com.example.dimoraapp.screens.SignInScreen
import com.example.dimoraapp.screens.SignUpScreen
import com.example.dimoraapp.screens.HomeScreen
import com.example.dimoraapp.screens.InfoScreen
import com.example.dimoraapp.screens.InviteContactsScreen
import com.example.dimoraapp.screens.MoreHouseScreen
import com.example.dimoraapp.screens.NotificationScreen
import com.example.dimoraapp.screens.ProfileScreen
import com.example.dimoraapp.screens.SearchScreen
import com.example.dimoraapp.utils.SessionManager
import com.example.dimoraapp.viewmodel.NotificationViewModel
import com.example.dimoraapp.viewmodel.NotificationViewModelFactory

@Composable
fun AppNavigation(context: Context) {
    val navController: NavHostController = rememberNavController()
    val sessionManager = SessionManager(context)
    val token = sessionManager.getToken()

    // --- Create your repository and ViewModel here! ---
    val repository = remember { NotificationRepository(RetrofitClient.api) }
    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(repository, token ?: "") as ViewModelProvider.Factory
    )
    val notificationCountState = notificationViewModel.notificationCount.collectAsState()
    val notificationCount = notificationCountState.value

    // Determine the start destination based on session validity
    val startDestination = if (sessionManager.isSessionValid()) {
        "homescreen" // Navigate to home if session is valid
    } else {
        "signin" // Navigate to sign-in if session is invalid
    }

    // Navigation Host
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("getstarted") { GetStartedScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("signin") { SignInScreen(navController) }
        composable("homescreen") { HomeScreen(
            navController = navController,
            notificationCount = notificationCount,
            onNotificationsClicked = { notificationViewModel.clearNotificationCount() }
        ) }
        composable("infoscreen/{adId}") { backStackEntry ->
            val adId = backStackEntry.arguments?.getString("adId")?.toIntOrNull()
            if (adId != null) {
                InfoScreen(
                    navController = navController,
                    adId = adId,
                    notificationCount = notificationCount,
                    onNotificationsClicked = { notificationViewModel.clearNotificationCount() }
                )
            }
        }
        composable("profilescreen"){ ProfileScreen(
            navController = navController,
            notificationCount = notificationCount,
            onNotificationsClicked = { notificationViewModel.clearNotificationCount() }
        ) }
        composable("searchscreen"){ SearchScreen(
            navController = navController,
            notificationCount = notificationCount,
            onNotificationsClicked = { notificationViewModel.clearNotificationCount() },
        ) }
        composable("notificationscreen"){ NotificationScreen(
            token = token ?: "",
            navController = navController,
            notificationViewModel = notificationViewModel,
            notificationCount = notificationCount,
            onNotificationsClicked = { notificationViewModel.clearNotificationCount() }
        ) }
        composable("morehousescreen/{category}"){ backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "latest"
            MoreHouseScreen(
            navController = navController,
            notificationCount = notificationCount,
            onNotificationsClicked = { notificationViewModel.clearNotificationCount() },
            category = category
        ) }
        composable("invite_contacts"){
            InviteContactsScreen(
                navController = navController,
                notificationCount = notificationCount,
                onNotificationsClicked = { notificationViewModel.clearNotificationCount() }
            )
        }
    }

    // Clear session and navigate to sign-in if session becomes invalid (optional)
    LaunchedEffect(Unit) {
        if (!sessionManager.isSessionValid()) {
            sessionManager.clearSession()
            navController.navigate("getstarted") {
                popUpTo(0) { inclusive = true } // Clear backstack
            }
        }
    }
}