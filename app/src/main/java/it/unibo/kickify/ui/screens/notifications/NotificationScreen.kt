package it.unibo.kickify.ui.screens.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.models.NotificationType
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.NotificationItem
import it.unibo.kickify.ui.composables.NotificationTitleLine
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.theme.BluePrimary

@Composable
fun NotificationScreen(
    navController: NavController
){
    ScreenTemplate(
        screenTitle = stringResource(R.string.notificationscreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
    ) { contentPadding ->
        val state = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(state),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            NotificationTitleLine("11 April 2025")
            NotificationItem(
                NotificationType.ProductBackinStock,
                notificationText = "Great news, Mario: the Converse Chuck 70 High All Star is back in stock!!",
                colorDot = BluePrimary,
               // onClick = onClick
            ) { }
            NotificationItem(
                NotificationType.OrderShipped,
                notificationText = "Your order #609327855 was handed over to the SDA express courier...",
                colorDot = BluePrimary,
                // onClick = onClick
            ) { }
            NotificationItem(
                NotificationType.FlashSale,
                notificationText = "Your favourite product Adidas Spezial is noew 20% off for a short time, ...",
                colorDot = BluePrimary,
                // onClick = onClick
            ) { }

            NotificationTitleLine("10 April 2025")
            NotificationItem(
                NotificationType.ItemsinCart,
                notificationText = "You spend a lot of time browsing but just spend a minute to make them...",
                colorDot = BluePrimary,
                // onClick = onClick
            ) { }
            NotificationItem(
                NotificationType.OrderPlaced,
                notificationText = "Your payment has been succesfully processed and we are starting to...",
                colorDot = Color.Gray,
                // onClick = onClick
            ) { }
            NotificationItem(
                NotificationType.RequestedProductReview,
                notificationText = "Share your experience with the product you purchased!",
                colorDot = BluePrimary,
                // onClick = onClick
            ) { }
        }
    }
}