package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.unibo.kickify.R
import it.unibo.kickify.data.models.NotificationType

@Composable
fun NotificationItem(
    notificationType: NotificationType,
    notificationText: String,
    colorDot: Color,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        NotificationImageFromType(notificationType)
        Spacer(Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = when(notificationType){
                        NotificationType.ProductBackinStock -> stringResource(R.string.notificationscreen_productBackInStock)
                        NotificationType.OrderShipped -> stringResource(R.string.notificationscreen_orderShipped)
                        NotificationType.FlashSale -> stringResource(R.string.notificationscreen_flashSale)
                        NotificationType.ItemsInCart -> stringResource(R.string.notificationscreen_productsInCart)
                        NotificationType.OrderPlaced -> stringResource(R.string.orderPlaced)
                        NotificationType.RequestedProductReview -> stringResource(R.string.notificationscreen_productReviewRequested)
                        NotificationType.OrderDelivered -> stringResource(R.string.delivered)
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.width(6.dp))
                NotificationDotIndicator(colorDot)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                notificationText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun NotificationImageFromType(notificationType: NotificationType){
    Image(
        painter = when(notificationType){
            NotificationType.ProductBackinStock -> painterResource(R.drawable.stock_light)
            NotificationType.OrderShipped -> painterResource(R.drawable.order_light)
            NotificationType.FlashSale -> painterResource(R.drawable.sale_light)
            NotificationType.ItemsInCart -> painterResource(R.drawable.cart_light)
            NotificationType.OrderPlaced -> painterResource(R.drawable.pack_light)
            NotificationType.RequestedProductReview -> painterResource(R.drawable.review_light)
            NotificationType.OrderDelivered -> painterResource(R.drawable.order_light)
        },
        contentDescription = "",
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(60.dp).padding(end = 6.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
fun NotificationDotIndicator(notificationColor: Color){
    Box(Modifier
        .size(11.dp)
        .clip(CircleShape)
        .background(notificationColor)
    )
}

@Composable
fun NotificationTitleLine(title: String){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 6.dp)
            .padding(top = 6.dp)
    ){
        Text(title, style = MaterialTheme.typography.titleLarge)
    }
}