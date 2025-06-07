package it.unibo.kickify.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.unibo.kickify.R

@Composable
fun CartItem(
    itemName: String, price: Double,
    size: Int, productColor: String,
    imageUrl: String
){
    Card(
        modifier = Modifier.height(110.dp)
            .padding(horizontal = 12.dp).fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(6.dp)
            )
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 6.dp)
                ){
                    Text(
                        itemName,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Size: $size")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    Text(
                        "€$price",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.width(10.dp))
                    ShoesColorIndicator( getColorFromString(productColor) )
                }
                Spacer(Modifier.height(5.dp))
                QuantityManager()
            }
            Spacer(Modifier.width(12.dp))
        }
    }
}

@Composable
fun QuantityManager(){
    var quantity by remember { mutableIntStateOf(1) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ){
        FilledIconButton(
            onClick = { if(quantity >= 2) quantity-=1 },
            enabled = quantity >= 2 ,
            modifier = Modifier.size(18.dp),
        ) {
            Icon(Icons.Outlined.Remove, contentDescription = "")
        }

        Text(text=quantity.toString())

        FilledIconButton(
            modifier = Modifier.size(18.dp),
            onClick = { if(quantity <= 9) quantity += 1 },
            enabled = quantity <= 9
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "")
        }

        Spacer(Modifier.width(18.dp))
        IconButton(onClick = {  }) {
            Icon(Icons.Outlined.Delete, contentDescription = "")
        }
    }
}

@Composable
fun CartAndCheckoutResume(subTotal: Double, shipping: Double, total: Double,
    onButtonClickAction: () -> Unit
){
    Card(
        modifier = Modifier.height(180.dp).fillMaxWidth()
            .padding(horizontal = 10.dp).padding(bottom = 4.dp)
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    stringResource(R.string.subtotal))
                Spacer(Modifier.width(20.dp))
                Text("€%.2f".format(subTotal))
            }
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 6.dp)
            ){
                Text(
                    stringResource(R.string.shipping),
                )
                Spacer(Modifier.width(20.dp))
                "%.2f".format(shipping)
                Text("€%.2f".format(shipping)
                )
            }
            Spacer(Modifier.width(30.dp))
            HorizontalDivider()
            Spacer(Modifier.width(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
            ){
                Text(stringResource(R.string.totalCost))
                Spacer(Modifier.width(20.dp))
                Text("€%.2f".format(total))
            }
            Row{
                Button(
                    onClick = { onButtonClickAction() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.cartscreen_checkOutBtn))
                }
            }
        }
    }
}

@Composable
fun ShoesColorIndicator(productColor: Color, indicatorSize: Dp = 20.dp){
    Box(Modifier
        .size(indicatorSize)
        .clip(CircleShape)
        .background(productColor)
    )
}

@Composable
fun getColorFromString(colorString: String): Color {
    return when(colorString){
        "Black" -> Color.Black
        "Blue" -> Color.Blue
        "Green" -> Color.Green
        "Purple" -> Color(0xFF800080)
        "Red" -> Color.Red
        "White" -> Color.White
        else -> Color.Transparent
    }
}