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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import it.unibo.kickify.R
import it.unibo.kickify.data.models.PaymentMethods

@Composable
fun OrdersTitleLine(title: String){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 6.dp)
            .padding(vertical = 6.dp)
    ){
        Text(title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun OrderIDCenterTitle(orderID:String){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(orderID,
            style = MaterialTheme.typography.bodyLarge
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

            OrderPaymentInfo(paymentMethod, totalPrice)

            TextButton(
                onClick = { actionDetailsButton() }
            ) {
                Text(text = stringResource(R.string.trackOrder))
            }
        }
    }
}

@Composable
fun OrderPaymentInfo(paymentMethod: PaymentMethods, totalPrice: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            paymentMethodIcon(paymentMethod),
            contentDescription = "",
            modifier = Modifier.size(50.dp)
                .padding(start = 6.dp)
        )
        Text(stringResource(R.string.myordersScreen_paidWith) + " " + paymentMethod.visibleName)
        Text(text = stringResource(R.string.myordersScreen_total) + " €%.2f".format(totalPrice))
    }
}

@Composable
fun OrderInfo(
    orderID: String,
    orderDate: String
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.order) + " " + orderID)
        Text(text = orderDate)
    }
}

@Composable
fun OrderItem(
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
        Image(
            modifier = Modifier.fillMaxWidth(fraction = 0.3f)
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(id = R.drawable.nike_zoom),
            contentDescription = "",
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
