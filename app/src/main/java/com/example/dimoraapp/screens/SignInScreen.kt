package com.example.dimoraapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dimoraapp.R
import com.example.dimoraapp.ui.theme.DMserif

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val username = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center // Centers content both vertically and horizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth() // Slightly reduce width for better UI
        ) {
            Text(
                text = stringResource(R.string.sin),
                fontSize = 60.sp,
                fontFamily = DMserif,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(32.dp))

            val padding = if (isLandscape) 64.dp else 16.dp
            listOf(
                "Username" to username
            ).forEach { (label, state) ->
                TextField(
                    value = state.value,
                    onValueChange = { state.value = it },
                    label = { Text(label) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = padding)
                        .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        focusedLabelColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(Modifier.height(16.dp))
            }
            var password by rememberSaveable { mutableStateOf("") }
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
                    .fillMaxWidth()
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

            Row {
                Text(text = "Don't have an account? ", fontSize = 14.sp, color = Color.Gray)
                ClickableText(
                    text = AnnotatedString("Sign up"),
                    onClick = { navController.navigate("signup") },
                    style = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                )
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("homescreen") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(80.dp),
            shape = CircleShape
        ) {
            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Navigate to home")
        }
    }
}
