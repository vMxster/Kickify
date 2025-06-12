package it.unibo.kickify.ui.composables

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.unibo.kickify.data.models.ShopCategory

@Composable
fun ProductCardWishlistPage(
    productName: String,
    mainImgUrl: String,
    price: Double,
    onClick: () -> Unit,
    onToggleWishlistIcon: (Boolean) -> Unit
){
    Card(
        onClick = onClick,
        modifier = Modifier.size(200.dp).fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            var checked by remember { mutableStateOf(true) }
            Column (
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                ProductImage(
                    productName = productName,
                    imgUrl = mainImgUrl,
                    size = 120.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                ProductNameText(
                    productName = productName,
                    textAlign = TextAlign.Center
                )
                ProductPriceText(price = price,
                    textAlign = TextAlign.Center)
            }

            IconToggleButton(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onToggleWishlistIcon(it)
                },
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = if(checked) Icons.Outlined.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = "",
                    tint = Color.Red,
                )
            }
        }

    }
}

@Composable
fun SquareProductCardHomePage(
    productID: Int,
    productName: String,
    mainImgUrl: String,
    price: Double,
    onClick: (Int) -> Unit
){
    Card(
        modifier = Modifier.size(200.dp).fillMaxSize()
            .padding(horizontal = 12.dp)
            .clickable { onClick(productID) } // Shoe ID to be passed
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProductImage(imgUrl = mainImgUrl, productName = productName)
            ProductNameText(productName)
            ProductPriceText(price)
        }
    }
}

@Composable
fun RectangularProductCardHomePage(
    productID: Int,
    productName: String,
    mainImgUrl: String,
    price: Double,
    onClick: (Int) -> Unit
){
    Card(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 12.dp)
            .clickable { onClick(productID) } // Shoe ID to be passed
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ){
                ProductNameText(
                    productName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(4.dp))
                ProductPriceText(price)
            }
            ProductImage(productName = productName, imgUrl = mainImgUrl)
        }
    }
}

@Composable
fun ProductCardShoesPage(
    productName: String,
    category: ShopCategory,
    price: Double,
    mainImgUrl: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(200.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProductImage(imgUrl = mainImgUrl, productName = productName,
                modifier = Modifier.fillMaxWidth(fraction = 0.8f))
            ProductNameText(productName)
            ProductCategoryText(category)
            ProductPriceText(price)
        }
    }
}

@Composable
fun ProductImage(
    imgUrl: String,
    productName: String,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
){
    AsyncImage(
        model = imgUrl,
        contentDescription = "$productName image",
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size)
    )
}

@Composable
fun ProductNameText(
    productName:String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    textAlign: TextAlign = TextAlign.Center,
    fontWeight: FontWeight = FontWeight.Bold
){
    Text(
        text = productName,
        style = style,
        textAlign = textAlign,
        fontWeight = fontWeight
    )
}

@Composable
fun ProductCategoryText(
    category: ShopCategory,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    textAlign: TextAlign = TextAlign.Center
){
    Text(
        text = category.toString(),
        style = style,
        textAlign = textAlign
    )
}

@Composable
fun ProductPriceText(
    price: Double,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    textAlign: TextAlign = TextAlign.Center
){
    Text(
        text = "€%.2f".format(price),
        style = style,
        textAlign = textAlign,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}