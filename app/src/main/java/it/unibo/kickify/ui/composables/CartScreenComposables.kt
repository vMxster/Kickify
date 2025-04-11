package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.unibo.kickify.R

@Composable
fun CartItem(itemName: String, price: Double, size: Int
){
    Card(
        //onClick = onClick,
        modifier = Modifier.size(300.dp, 90.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painterResource(R.drawable.nike_pegasus),
                "",
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
                        .padding(top = 6.dp)
                        .padding(end = 6.dp)
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

        Spacer(Modifier.width(6.dp))
        Text(text=quantity.toString())

        Spacer(Modifier.width(6.dp))
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
fun CartResume(subTotal: Double, shipping: Double){
    Card(
        //onClick = onClick,
        modifier = Modifier.size(300.dp, 160.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =  MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 12.dp)
                .padding(vertical = 6.dp),
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    stringResource(R.string.cartscreen_subtotal),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(20.dp))
                Text("€%.2f".format(subTotal),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 6.dp)
            ){
                Text(
                    stringResource(R.string.cartscreen_shipping),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(20.dp))
                "%.2f".format(shipping)
                Text("€%.2f".format(shipping),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(30.dp))
            HorizontalDivider()
            Spacer(Modifier.width(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 6.dp)
            ){
                Text(
                    stringResource(R.string.cartscreen_totalCost),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(20.dp))
                Text("€%.2f".format((subTotal+shipping)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row{
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.cartscreen_checkOutBtn))
                }
            }
        }
    }
}