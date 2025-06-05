package com.example.dimoraapp.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dimoraapp.screens.GetStartedScreen
import com.example.dimoraapp.screens.SignInScreen
import com.example.dimoraapp.screens.SignUpScreen
import com.example.dimoraapp.screens.HomeScreen
import com.example.dimoraapp.screens.InfoScreen
import com.example.dimoraapp.screens.MoreHouseScreen
import com.example.dimoraapp.screens.NotificationScreen
import com.example.dimoraapp.screens.ProfileScreen
import com.example.dimoraapp.screens.SearchScreen
import com.example.dimoraapp.utils.SessionManager

@Composable
fun AppNavigation(context: Context) {
    val navController: NavHostController = rememberNavController()

    // Initialize SessionManager
    val sessionManager = SessionManager(context)

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
        composable("homescreen") { HomeScreen(navController) }
        composable("infoscreen"){ InfoScreen(navController) }
        composable("profilescreen"){ ProfileScreen(navController) }
        composable("searchscreen"){ SearchScreen(navController) }
        composable("notificationscreen"){ NotificationScreen(navController) }
        composable("morehousescreen"){ MoreHouseScreen(navController) }
    }

    // Clear session and navigate to sign-in if session becomes invalid (optional)
    LaunchedEffect(Unit) {
        if (!sessionManager.isSessionValid()) {
            sessionManager.clearSession()
            navController.navigate("signin") {
                popUpTo(0) { inclusive = true } // Clear backstack
            }
        }
    }
}