package com.example.dimoraapp.navigation

import androidx.compose.runtime.Composable
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

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "getstarted"
    ) {
        composable("getstarted") { GetStartedScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("signin") { SignInScreen(navController) }
        composable("homescreen") { HomeScreen(navController) }
        composable("infoscreen"){ InfoScreen(navController) }
        composable("profilescreen"){ ProfileScreen(navController) }
        composable("searchscreen"){ SearchScreen(navController) }
        composable("notificationscreen"){ NotificationScreen(navController)}
        composable("morehousescreen"){ MoreHouseScreen(navController)}
    }
}
