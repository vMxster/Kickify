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
            .padding(horizontal = 6.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        NotificationImageFromType(notificationType, darkTheme = true)

        Spacer(Modifier.width(6.dp))
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
                Text(text = when(notificationType){
                    NotificationType.ProductBackinStock -> stringResource(R.string.notificationscreen_productBackInStock)
                    NotificationType.OrderShipped -> stringResource(R.string.notificationscreen_orderShipped)
                    NotificationType.FlashSale -> stringResource(R.string.notificationscreen_flashSale)
                    NotificationType.ItemsinCart -> stringResource(R.string.notificationscreen_productsInCart)
                    NotificationType.OrderPlaced -> stringResource(R.string.notificationscreen_orderPlaced)
                    NotificationType.RequestedProductReview -> stringResource(R.string.notificationscreen_productReviewRequested)
                },
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.width(6.dp))
                NotificationDotIndicator(colorDot)
            }
            Spacer(Modifier.height(6.dp))
            Text(notificationText,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun NotificationImageFromType(notificationType: NotificationType, darkTheme: Boolean
){
    Image(
        painter = if(darkTheme) {
                        when(notificationType){
                        NotificationType.ProductBackinStock -> painterResource(R.drawable.stock_dark)
                        NotificationType.OrderShipped -> painterResource(R.drawable.order_dark)
                        NotificationType.FlashSale -> painterResource(R.drawable.sale_dark)
                        NotificationType.ItemsinCart -> painterResource(R.drawable.cart_dark)
                        NotificationType.OrderPlaced -> painterResource(R.drawable.pack_black)
                        NotificationType.RequestedProductReview -> painterResource(R.drawable.review_dark)}
                    } else {
                        when(notificationType){
                        NotificationType.ProductBackinStock -> painterResource(R.drawable.stock_light)
                        NotificationType.OrderShipped -> painterResource(R.drawable.order_light)
                        NotificationType.FlashSale -> painterResource(R.drawable.sale_light)
                        NotificationType.ItemsinCart -> painterResource(R.drawable.cart_light)
                        NotificationType.OrderPlaced -> painterResource(R.drawable.pack_light)
                        NotificationType.RequestedProductReview -> painterResource(R.drawable.review_light)
                    }
        },
        contentDescription = "",
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(60.dp)
    )
}

@Composable
fun NotificationDotIndicator(productColor: Color){
    Box(Modifier
        .size(7.dp)
        .clip(CircleShape)
        .background(productColor)
    )
}

@Composable
fun NotificationTitleLine(title: String){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 6.dp)
    ){
        Text(title,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge)
    }
}