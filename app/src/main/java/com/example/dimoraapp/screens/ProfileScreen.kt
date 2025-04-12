package com.example.dimoraapp.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dimoraapp.R
import com.example.dimoraapp.navigation.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController){
    var isDrawerOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                            item { content(navController) }
                        }
                    }
                )
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
fun content(navController: NavController) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape)
    Row(
        modifier = Modifier.padding(start = 100.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Profilepicture()
        ProfileDetails(onClick = { navController.navigate("signin")})
    }
    else
        Column {
            Profilepicture()
            ProfileDetails(onClick = { navController.navigate("signin")})
        }
}

@Composable
fun Profilepicture () {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape)
    Box(
        modifier = Modifier
            .height(300.dp),
        contentAlignment = Alignment.Center
    ){
        Image(
            modifier = Modifier.clip(CircleShape),
            painter = painterResource(R.drawable.profile),
            contentDescription = "profile",
            alignment = Alignment.Center
        )
    }
    else
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ){
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
fun ProfileDetails(onClick: () -> Unit) {
    val email = remember { mutableStateOf("example@gmail.com") }
    val username = remember { mutableStateOf("user123") }
    val contact = remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp
    val width = if (isLandscape) 600.dp else 500.dp
    Column(
        modifier = Modifier
            .padding(top = 24.dp, start = padding, end = padding)
    ) {
        // Define a list of pairs (label, state)
        val formFields = listOf(
            "Email" to email,
            "Username" to username,
            "Contact" to contact,
        )

        // Loop through the list and create a TextField for each pair
        formFields.forEach { (label, state) ->
            TextField(
                value = state.value,
                onValueChange = { state.value = it },
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

        var password by rememberSaveable { mutableStateOf("12345678") }
        var visiblity by remember { mutableStateOf(false) }
        var icon = if (visiblity) painterResource(R.drawable.baseline_visibility_24)
        else painterResource(R.drawable.baseline_visibility_off_24)

        TextField(
            password, onValueChange = { password = it},
            placeholder = { Text("Password") },
            label = { Text("Password") },
            trailingIcon = {
                IconButton(onClick = { visiblity = !visiblity }) {
                    Icon(painter = icon
                    , contentDescription = "visible")
                }
            },
            visualTransformation = if (visiblity) VisualTransformation.None
            else PasswordVisualTransformation(),
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
            ){
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