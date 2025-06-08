package com.example.dimoraapp.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dimoraapp.R
import com.example.dimoraapp.navigation.BottomNavBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

data class Contact(val name: String, val phone: String)

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InviteContactsScreen(
    navController: NavController,
    notificationCount: Int,
    onNotificationsClicked: () -> Unit
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var isDrawerOpen by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val profileImagePath = getSavedProfileImagePath(context)

    // Request permission and load contacts
    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            contacts = loadContacts(context)
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    Scaffold(
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!permissionState.status.isGranted) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Contacts permission required to invite users")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                ) {
                    items(contacts.size) { idx ->
                        val contact = contacts[idx]
                        ContactInviteRow(contact)
                    }
                }
            }
        }
    }
}

@Composable
fun ContactInviteRow(contact: Contact) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = contact.name, modifier = Modifier.weight(1f))
        Button(onClick = {
            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:${contact.phone}")
                putExtra("sms_body", "Hi ${contact.name}, I invite you to try out DimoraApp!")
            }
            context.startActivity(smsIntent)
        }) {
            Text("Invite")
        }
    }
}

fun loadContacts(context: Context): List<Contact> {
    val contacts = mutableListOf<Contact>()
    val contentResolver = context.contentResolver
    val cursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        ),
        null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )
    cursor?.use {
        val nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        while (cursor.moveToNext()) {
            val name = cursor.getString(nameIdx)
            val phone = cursor.getString(phoneIdx)
            contacts.add(Contact(name, phone))
        }
    }
    return contacts
}