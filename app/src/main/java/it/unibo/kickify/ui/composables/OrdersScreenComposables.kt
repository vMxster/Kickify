package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.unibo.kickify.R
import it.unibo.kickify.data.models.PaymentMethods

@Composable
fun OrdersTitleLine(title: String){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ){
        Text(title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun OrderCardContainer(
    orderID: String,
    orderDate: String,
    paymentMethod: PaymentMethods,
    totalPrice: Float,
    actionDetailsButton: () -> Unit,
    items: @Composable () -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            OrderInfo(orderID, orderDate)

            items()

            OrderPaymentInfo(paymentMethod)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = { actionDetailsButton() }
                ) {
                    Text(text = stringResource(R.string.trackOrder))
                }
                Text(text = stringResource(R.string.myordersScreen_total) + " €%.2f".format(totalPrice),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun OrderPaymentInfo(paymentMethod: PaymentMethods) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            paymentMethodIcon(paymentMethod),
            contentDescription = "",
            modifier = Modifier.size(50.dp).padding(horizontal = 6.dp)
        )
        Text(
            stringResource(R.string.myordersScreen_paidWith) + " " + paymentMethod.visibleName,
            modifier = Modifier.fillMaxWidth(fraction = 0.8f),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun OrderInfo(orderID: String, orderDate: String){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.order) + " #$orderID")
        Text(text = orderDate)
    }
}

@Composable
fun OrderItem(
    imageUrl: String,
    productName: String,
    productSize: String,
    qty: Int,
    colorString: String,
    finalPrice: Float,
    originalPrice: Float? = null
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            modifier = Modifier.fillMaxWidth(fraction = 0.3f)
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentDescription = "",
            placeholder = rememberVectorPainter(Icons.Outlined.Image)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = productName, style = MaterialTheme.typography.bodyMedium)
            SizeAndQuantityText(productSize, qty)
            ProductColorText(colorString)
            if(originalPrice == null){ // no discount to show
                ProductFinalPriceText(finalPrice)
            } else { // show final and original price
                ProductFinalAndOriginalPrice(finalPrice, originalPrice)
            }
        }
    }
}

@Composable
fun SizeAndQuantityText(productSize: String, qty: Int){
    Text(text = stringResource(R.string.size) + ": " + productSize
            + " | " + stringResource(R.string.qty) + ": " + qty)
}

@Composable
fun ProductColorText(colorString: String){
    Text(text = stringResource(R.string.color)+ ": " + colorString)
}

@Composable
fun ProductFinalAndOriginalPrice(finalPrice: Float, originalPrice: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        ProductFinalPriceText(finalPrice)
        Spacer(Modifier.width(10.dp))
        ProductOriginalPriceText(originalPrice)
    }
}

@Composable
fun ProductFinalPriceText(price: Float, textColor: Color = MaterialTheme.colorScheme.onBackground){
    Text(text = "€%.2f".format(price),
        color = textColor
    )
}

@Composable
fun ProductOriginalPriceText(originalPrice: Float){
    Text(text = "€%.2f".format(originalPrice),
        style = TextStyle(
            textDecoration = TextDecoration.LineThrough,
        )
    )
}
