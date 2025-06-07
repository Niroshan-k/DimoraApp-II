package com.example.dimoraapp.screens

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.provider.MediaStore
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.SignalCellularAlt
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
import coil.compose.rememberAsyncImagePainter
import com.example.dimoraapp.R
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.screens.SideNavBar
import com.example.dimoraapp.screens.TopNavBar
import com.example.dimoraapp.viewmodel.ProfileViewModel
import com.example.dimoraapp.data.model.ProfileState
import com.example.dimoraapp.data.repositor.AuthRepository
import com.example.dimoraapp.utils.SessionManager
import com.example.dimoraapp.data.api.RetrofitClient
import com.example.dimoraapp.viewmodel.ProfileViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

// --- UTILS FOR IMAGE STORAGE ---
fun saveProfileImageToInternalStorage(context: Context, bitmap: Bitmap): String {
    val dir = context.filesDir
    val oldFile = File(dir, "profile_picture.jpg")
    if (oldFile.exists()) oldFile.delete()
    val file = File(context.filesDir, "profile_picture.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file.absolutePath
}
fun getSavedProfileImagePath(context: Context): String? {
    val file = File(context.filesDir, "profile_picture.jpg")
    return if (file.exists()) file.absolutePath else null
}
fun removeProfileImage(context: Context) {
    val file = File(context.filesDir, "profile_picture.jpg")
    if (file.exists()) file.delete()
}
// --- END UTILS ---

// --- SYSTEM INFO HELPERS ---
fun getNetworkDetails(context: Context): Pair<String, String> {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork
    val capabilities = cm.getNetworkCapabilities(network)
    return when {
        capabilities == null -> "Offline" to "No connection"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ssid = try {
                val info = wifiManager.connectionInfo
                val ssidStr = info?.ssid?.replace("\"", "") ?: "Unknown"
                "Wi-Fi ($ssidStr)"
            } catch (e: Exception) {
                "Wi-Fi"
            }
            ssid to "Connected"
        }
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val networkType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
            ) {
                when (tm.networkType) {
                    TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
                    TelephonyManager.NETWORK_TYPE_NR -> "5G"
                    TelephonyManager.NETWORK_TYPE_HSPAP -> "H+"
                    TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
                    TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
                    else -> "Mobile Data"
                }
            } else {
                "Permission Required"
            }
            "Mobile ($networkType)" to "Connected"
        }
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet" to "Connected"
        else -> "Connected" to "Unknown"
    }
}

fun getBatteryPercentage(context: Context): Int {
    val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus = context.registerReceiver(null, ifilter)
    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    return if (level >= 0 && scale > 0) ((level / scale.toFloat()) * 100).toInt() else -1
}
// --- END SYSTEM INFO HELPERS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    notificationCount: Int,
    onNotificationsClicked: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val repository = AuthRepository(
        api = RetrofitClient.api,
        sessionManager = sessionManager
    )
    val application = context.applicationContext as Application
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(application, repository)
    )

    val profileState by viewModel.profileState.collectAsState()
    val offlineProfile by viewModel.offlineProfile.collectAsState()

    // Snackbar State
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // For picture picker
    var profileImagePath by remember { mutableStateOf(getSavedProfileImagePath(context)) }

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    var isDrawerOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopNavBar(
                        onMenuClick = { isDrawerOpen = true },
                        scrollBehavior = scrollBehavior
                    )
                },
                bottomBar = {
                    BottomNavBar(
                        navController = navController,
                        notificationCount = notificationCount,
                        onNotificationsClicked = onNotificationsClicked,
                        profileImagePath = profileImagePath
                    )
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { paddingValues ->
                Box(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    // Wrap main content in a scrollable container!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        when {
                            profileState.isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            profileState.error != null -> {
                                if (offlineProfile != null) {
                                    OfflineProfileContent(
                                        navController = navController,
                                        profileState = offlineProfile!!,
                                        onLogOut = {
                                            sessionManager.clearSession()
                                            navController.navigate("signin") {
                                                popUpTo("profilescreen") { inclusive = true }
                                            }
                                        },
                                        onSaveOffline = {
                                            viewModel.saveProfileOffline(offlineProfile!!)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Saved locally")
                                            }
                                        },
                                        onUpdate = { viewModel.fetchProfile() },
                                        profileImagePath = profileImagePath,
                                        onProfileImageChanged = { profileImagePath = it }
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(top = 100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = profileState.error ?: "Unknown Error",
                                            color = Color.Red
                                        )
                                    }
                                }
                            }
                            else -> {
                                ProfileContent(
                                    navController = navController,
                                    profileState = profileState,
                                    onLogOut = {
                                        sessionManager.clearSession()
                                        navController.navigate("signin") {
                                            popUpTo("profilescreen") { inclusive = true }
                                        }
                                    },
                                    onSaveOffline = {
                                        viewModel.saveProfileOffline(profileState)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Saved locally")
                                        }
                                    },
                                    onUpdate = { viewModel.fetchProfile() },
                                    profileImagePath = profileImagePath,
                                    onProfileImageChanged = { profileImagePath = it }
                                )
                            }
                        }
                    }
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
fun ProfileContent(
    navController: NavController,
    profileState: ProfileState,
    onLogOut: () -> Unit,
    onSaveOffline: () -> Unit,
    onUpdate: () -> Unit,
    profileImagePath: String?,
    onProfileImageChanged: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePicturePicker(
                modifier = Modifier
                    .padding(end = 40.dp)
                    .size(170.dp),
                profileImagePath = profileImagePath,
                onImageChanged = onProfileImageChanged
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileDetails(
                    email = profileState.email,
                    username = profileState.username,
                    contact = profileState.contact,
                    onSaveOffline = onSaveOffline,
                    onLogOut = onLogOut,
                    onUpdate = onUpdate
                )
                SystemInfoSection(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePicturePicker(
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 16.dp)
                    .size(170.dp),
                profileImagePath = profileImagePath,
                onImageChanged = onProfileImageChanged
            )
            ProfileDetails(
                email = profileState.email,
                username = profileState.username,
                contact = profileState.contact,
                onSaveOffline = onSaveOffline,
                onLogOut = onLogOut,
                onUpdate = onUpdate
            )
            SystemInfoSection(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun OfflineProfileContent(
    navController: NavController,
    profileState: ProfileState,
    onLogOut: () -> Unit,
    onSaveOffline: () -> Unit,
    onUpdate: () -> Unit,
    profileImagePath: String?,
    onProfileImageChanged: (String) -> Unit
) {
    ProfileContent(
        navController = navController,
        profileState = profileState,
        onLogOut = onLogOut,
        onSaveOffline = onSaveOffline,
        onUpdate = onUpdate,
        profileImagePath = profileImagePath,
        onProfileImageChanged = onProfileImageChanged
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetails(
    email: String,
    username: String,
    contact: String,
    onSaveOffline: () -> Unit,
    onLogOut: () -> Unit,
    onUpdate: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp
    val width = if (isLandscape) 600.dp else 500.dp

    Column(
        modifier = Modifier
            .padding(top = 24.dp, start = padding, end = padding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
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
                    .padding(bottom = 16.dp)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(end = 8.dp)
                    .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                onClick = onUpdate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Update", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(start = 8.dp)
                    .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                onClick = onLogOut,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Log Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium),
            onClick = onSaveOffline,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Save Profile Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfilePicturePicker(
    modifier: Modifier = Modifier,
    profileImagePath: String?,
    onImageChanged: (String) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let {
            val path = saveProfileImageToInternalStorage(context, it)
            onImageChanged(path)
        }
    }
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            val bmp = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            val path = saveProfileImageToInternalStorage(context, bmp)
            onImageChanged(path)
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(Color.LightGray)
            .size(170.dp),
        contentAlignment = Alignment.Center
    ) {
        if (profileImagePath != null && File(profileImagePath).exists()) {
            Image(
                painter = rememberAsyncImagePainter(profileImagePath),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-12).dp, y = (-12).dp)
                .size(40.dp)
                .background(Color.Black, shape = CircleShape)
                .clip(CircleShape)
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "Set Profile Picture",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            },
            text = {
                Text(
                    "Choose the image source",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        cameraLauncher.launch(null)
                        showDialog = false
                    }
                ) {
                    Text("Camera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                        showDialog = false
                    }
                ) {
                    Text("Gallery")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            tonalElevation = 8.dp
        )
    }
}

// --- SYSTEM INFO SECTION ---
@Composable
fun SystemInfoSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var networkPair by remember { mutableStateOf(getNetworkDetails(context)) }
    var batteryPct by remember { mutableStateOf(getBatteryPercentage(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            networkPair = getNetworkDetails(context)
            batteryPct = getBatteryPercentage(context)
            delay(5000)
        }
    }

    val androidVersion = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    val batteryStatusMsg =
        if (batteryPct in 0..20)
            "Battery is low ($batteryPct%). Connect your device to a charger!"
        else null

    val infoList = listOf(
        Triple(Icons.Default.NetworkWifi, "Network", networkPair.first),
        Triple(Icons.Default.SignalCellularAlt, "Network Status", networkPair.second),
        Triple(Icons.Default.BatteryStd, "Battery", if (batteryPct >= 0) "$batteryPct%" else "N/A"),
        Triple(Icons.Default.Info, "Android Version", androidVersion),
        Triple(Icons.Default.Info, "Device Model", Build.MODEL),
    )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 0.dp)
            .heightIn(max = 300.dp), // makes info card scrollable
        userScrollEnabled = true
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Device Info",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                }
            }
        }
        items(infoList) { (icon, label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
        if (!batteryStatusMsg.isNullOrBlank()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    batteryStatusMsg,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }
}