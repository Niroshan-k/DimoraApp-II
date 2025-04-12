package com.example.dimoraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import com.example.dimoraapp.navigation.AppNavigation
import com.example.dimoraapp.ui.theme.DimoraAppTheme

import com.example.dimoraapp.screens.InfoScreen
import com.example.dimoraapp.ui.theme.CustomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            CustomTheme {
                AppNavigation()
            }
        }
    }
}
