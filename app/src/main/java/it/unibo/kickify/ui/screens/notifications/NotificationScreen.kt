package it.unibo.kickify.ui.screens.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.unibo.kickify.data.models.NotificationType
import it.unibo.kickify.ui.composables.NotificationItem
import it.unibo.kickify.ui.composables.NotificationTitleLine
import it.unibo.kickify.ui.theme.BluePrimary
import it.unibo.kickify.ui.theme.KickifyTheme

@Preview(backgroundColor = 0xFFFFFF)
@Composable
fun NotificationScreen(
    //onClick: () -> Unit
){
    KickifyTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
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