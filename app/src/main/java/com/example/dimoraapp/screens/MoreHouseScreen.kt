package com.example.dimoraapp.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dimoraapp.R
import com.example.dimoraapp.navigation.BottomNavBar
import com.example.dimoraapp.ui.theme.DMserif

@Composable
fun MoreHouseScreen(
    navController: NavController,
    notificationCount: Int,
    onNotificationsClicked: () -> Unit
) {
    var isDrawerOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {

        // Content layout
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // LazyColumn for scrollable content
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Take up remaining vertical space
                    .fillMaxWidth(),
            ) {
                item {
                    TopNavBarInfo(
                        goToHomePage = { navController.navigate("homescreen")},
                        onMenuClick = { isDrawerOpen = true })
                }
                item {
                    Cardlist(navController)
                }

            }

            // Bottom navigation bar
            BottomNavBar(
                navController = navController,
                notificationCount = notificationCount,
                onNotificationsClicked = onNotificationsClicked
            )
        }

        // Side drawer for navigation
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInHorizontally(initialOffsetX = { -300 }),
            exit = slideOutHorizontally(targetOffsetX = { -300 })
        ) {
            SideNavBar(
                onClose = { isDrawerOpen = false },
                onAboutUsClick = { navController.navigate("about_us") }
            )
        }
    }
}


@Composable
fun HouseCard(onClick: () -> Unit, picture:Painter, name:String, price:String) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 64.dp else 16.dp
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(start = padding,8.dp, end = padding, bottom = 8.dp)
            .fillMaxWidth()
            .clickable{onClick()}
            .shadow(6.dp, shape = RoundedCornerShape(8.dp)),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = picture,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(100.dp).height(100.dp).clip(shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ){
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = price, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        fontFamily = DMserif
                    )
                    Text(
                        text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }
}

@Composable
fun Cardlist(navController: NavController) {
    val houseList = listOf(
        Triple(R.drawable.image1, R.string.Heading1, R.string.price1),
        Triple(R.drawable.image2, R.string.Heading2, R.string.price2),
        Triple(R.drawable.image3, R.string.Heading3, R.string.price3),
        Triple(R.drawable.image4, R.string.Heading4, R.string.price4),
    )

    houseList.forEach { (image, heading, price) ->
        HouseCard(
            onClick = { navController.navigate("infoscreen") },
            painterResource(image),
            stringResource(heading),
            stringResource(price)
        )
    }
}
