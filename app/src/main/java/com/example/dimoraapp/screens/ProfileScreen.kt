package com.example.dimoraapp.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dimoraapp.R
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.viewmodel.ProfileViewModel
import com.example.dimoraapp.data.model.ProfileState
import com.example.dimoraapp.data.repositor.AuthRepository
import com.example.dimoraapp.utils.SessionManager
import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.viewmodel.ProfileViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    // Retrieve the context
    val context = LocalContext.current

    // Initialize the SessionManager
    val sessionManager = SessionManager(context)

    // Initialize the repository
    val repository = AuthRepository(
        api = RetrofitClient.api,
        sessionManager = sessionManager
    )

    // Use the ProfileViewModelFactory to create the ViewModel
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(repository)
    )

    // Observe the profile state and render UI
    val profileState by viewModel.profileState.collectAsState()

    // Fetch the profile when the screen is loaded
    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    // Drawer State
    var isDrawerOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (profileState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (profileState.error != null) {
                Text(
                    text = profileState.error ?: "Unknown Error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = { TopNavBar(onMenuClick = { isDrawerOpen = true }, scrollBehavior = scrollBehavior) },
                        bottomBar = { BottomNavBar(navController = navController) },
                        content = { paddingValues ->
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(paddingValues),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    ProfileContent(
                                        navController = navController,
                                        profileState = profileState,
                                        onLogOut = {
                                            sessionManager.clearSession() // Clear the session
                                            navController.navigate("signin") { // Navigate to the sign-in screen
                                                popUpTo("profilescreen") { inclusive = true } // Clear backstack
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = isDrawerOpen,
                enter = slideInHorizontally(initialOffsetX = { -300 }),
                exit = slideOutHorizontally(targetOffsetX = { -300 })
            ) {
                SideNavBar(
                    onClose = { isDrawerOpen = false },
                    onAboutUsClick = { navController.navigate("profilescreen") }
                )
            }
        }
    }
}

@Composable
fun ProfileContent(navController: NavController, profileState: ProfileState, onLogOut: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier.padding(start = 100.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            ProfilePicture()
            ProfileDetails(
                email = profileState.email ?: "N/A",
                username = profileState.username ?: "N/A",
                contact = profileState.contact ?: "N/A",
                onClick = onLogOut
            )
        }
    } else {
        Column {
            ProfilePicture()
            ProfileDetails(
                email = profileState.email ?: "N/A",
                username = profileState.username ?: "N/A",
                contact = profileState.contact ?: "N/A",
                onClick = onLogOut
            )
        }
    }
}

@Composable
fun ProfilePicture() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.clip(CircleShape),
            painter = painterResource(R.drawable.profile),
            contentDescription = "profile",
            alignment = Alignment.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetails(email: String, username: String, contact: String, onClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp
    val width = if (isLandscape) 600.dp else 500.dp

    Column(
        modifier = Modifier
            .padding(top = 24.dp, start = padding, end = padding)
    ) {
        val formFields = listOf(
            "Email" to email,
            "Username" to username,
            "Contact" to contact,
        )

        formFields.forEach { (label, value) ->
            TextField(
                value = value,
                onValueChange = {},
                label = { Text(label) },
                modifier = Modifier
                    .width(width)
                    .height(70.dp)
                    .padding(start = padding, end = padding, bottom = 16.dp)
                    .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    focusedLabelColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Box(
                modifier = Modifier.width(250.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Button(
                    modifier = Modifier
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
                    Text(text = "Update", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(
                modifier = Modifier.width(250.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = padding, end = padding)
                        .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                    onClick = { onClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Log Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}