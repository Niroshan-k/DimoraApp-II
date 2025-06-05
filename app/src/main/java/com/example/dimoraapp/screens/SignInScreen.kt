package com.example.dimoraapp.screens

import android.content.res.Configuration
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dimoraapp.viewmodel.SignInViewModel
import com.example.dimoraapp.R
import com.example.dimoraapp.ui.theme.DMserif
import com.example.dimoraapp.utils.SessionManager
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val email = remember { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    val viewModel: SignInViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) } // Initialize SessionManager

    // Error states
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) } // For backend general errors

    // Check if session is valid
//    if (sessionManager.isSessionValid()) {
//        // If session is valid, navigate directly to the home screen
//        navController.navigate("homescreen") {
//            popUpTo("signin") { inclusive = true } // Remove SignIn from the back stack
//            launchSingleTop = true
//        }
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.sin),
                fontSize = 60.sp,
                fontFamily = DMserif,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(32.dp))

            val padding = if (isLandscape) 64.dp else 16.dp

            // Email Field
            TextField(
                value = email.value,
                onValueChange = {
                    email.value = it
                    emailError = null // Clear error when user types
                    generalError = null // Clear any general error
                },
                label = { Text("Email") },
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
            // Email Error
            if (emailError != null) {
                Text(
                    text = emailError ?: "",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Password Field
            val icon = if (visibility) {
                painterResource(R.drawable.baseline_visibility_24)
            } else {
                painterResource(R.drawable.baseline_visibility_off_24)
            }

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null // Clear error when user types
                    generalError = null // Clear any general error
                },
                label = { Text("Password") },
                trailingIcon = {
                    IconButton(onClick = { visibility = !visibility }) {
                        Icon(
                            painter = icon,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(start = padding, end = padding, bottom = 4.dp)
                    .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    focusedLabelColor = MaterialTheme.colorScheme.surface
                )
            )
            // Password Error
            if (passwordError != null) {
                Text(
                    text = passwordError ?: "",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(alignment = Alignment.Start)
                        .padding(start = 16.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

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

            // General Error (Backend)
            if (generalError != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = generalError ?: "",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        FloatingActionButton(
            onClick = {
                // Validate email and password
                val valid = validateInputs(email.value, password, onEmailError = {
                    emailError = it
                }, onPasswordError = {
                    passwordError = it
                })

                if (valid) {
                    // Proceed with Sign-In API call
                    viewModel.signIn(
                        email = email.value,
                        password = password
                    ) { success, token ->
                        if (success) {
                            sessionManager.saveSession(email.value, token) // Save session
                            Toast.makeText(context, "Sign-in successful!", Toast.LENGTH_SHORT).show()
                            navController.navigate("homescreen") {
                                popUpTo("signin") { inclusive = true } // Remove SignIn from the back stack
                            }
                        } else {
                            // Parse backend error and display it
                            val errorMessage = parseBackendError(token)
                            generalError = errorMessage
                        }
                    }
                }
            },
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

// Function to validate inputs
private fun validateInputs(
    email: String,
    password: String,
    onEmailError: (String?) -> Unit,
    onPasswordError: (String?) -> Unit
): Boolean {
    var isValid = true

    if (email.isBlank()) {
        onEmailError("Email is required")
        isValid = false
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("Invalid email format")
        isValid = false
    }

    if (password.isBlank()) {
        onPasswordError("Password is required")
        isValid = false
    } else if (password.length < 0) {
        onPasswordError("Password must be at least 8 characters long")
        isValid = false
    }

    return isValid
}

// Function to parse backend error response
private fun parseBackendError(response: String): String {
    return try {
        val jsonObject = JSONObject(response)
        jsonObject.getString("error") // Assume the backend sends an "error" key
    } catch (e: Exception) {
        "Credentials must be wrong :("
    }
}