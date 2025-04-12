package com.example.dimoraapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.dimoraapp.ui.theme.DMserif
import com.example.dimoraapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val email = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val contact = remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .align(alignment = Alignment.Center)
                .background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                var height = if (isLandscape) 64.dp else 250.dp
                Spacer(Modifier.height(height))
                Text(
                    text = stringResource(R.string.sup),
                    fontSize = 60.sp,
                    fontFamily = DMserif,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            val padding = if (isLandscape) 64.dp else 16.dp
            items(listOf(
                "Email" to email,
                "Username" to username,
                "Contact" to contact,
            )) { (label, state) ->
                TextField(

                    value = state.value,
                    onValueChange = { state.value = it },
                    label = { Text(label) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(start = padding, end = padding)
                        .shadow(4.dp, shape = MaterialTheme.shapes.medium)
                        .align(alignment = Alignment.BottomEnd),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        focusedLabelColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
            item {
                var password by rememberSaveable { mutableStateOf("") }
                var visiblity by remember { mutableStateOf(false) }
                var icon = if (visiblity) painterResource(R.drawable.baseline_visibility_24)
                else painterResource(R.drawable.baseline_visibility_off_24)

                TextField(
                    password, onValueChange = { password = it},
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
                var confirmpassword by rememberSaveable { mutableStateOf("") }
                var visiblity2 by remember { mutableStateOf(false) }
                var icon2 = if (visiblity2) painterResource(R.drawable.baseline_visibility_24)
                else painterResource(R.drawable.baseline_visibility_off_24)

                TextField(
                    confirmpassword, onValueChange = { confirmpassword = it},
                    label = { Text("Confirm Password") },
                    trailingIcon = {
                        IconButton(onClick = { visiblity2 = !visiblity2 }) {
                            Icon(painter = icon2
                                , contentDescription = "visible")
                        }
                    },
                    visualTransformation = if (visiblity2) VisualTransformation.None
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
            }
            item {
                Row {
                    Text(text = "Already have an account? ", fontSize = 14.sp, color = Color.Gray)
                    ClickableText(
                        text = AnnotatedString("Sign in"),
                        onClick = { navController.navigate("signin") },
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    )
                }
                Spacer(Modifier.height(32.dp))
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("signin") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).size(80.dp),
            shape = CircleShape
        ) {
            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Navigate to sign in")
        }
    }
}
