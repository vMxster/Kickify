package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.kickify.R
import it.unibo.kickify.data.models.ShopCategory

@Composable
fun HomeScreenSmallBrandLogos(brandName: Int, onClick: () -> Unit) {
    Image(
        painter = painterResource(brandName),
        "",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
    )
    Spacer(Modifier.width(14.dp))
}

@Composable
fun HomeScreenCategory(categoryName: ShopCategory, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.padding(horizontal = 4.dp),
        colors =  ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(categoryName.toString())
    }
}

@Composable
fun HomeScreenSectionSquareProductCards(
    sectionTitle:String,
    /** associates the product with a boolean: true to make a rectangular product card, false to make a square product card*/
    prodList: Map<String, Boolean>,
    modifier: Modifier
    /*onClick: () -> Unit*/
){

    Column (
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row (modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { /*onClick()*/ }) {
                Text(stringResource(R.string.homescreen_seeAll))
            }
        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            val price = 97.99
            for(prod in prodList){
                if(!prod.value){ // to make a square product card
                    SquareProductCardHomePage(
                        productName = prod.key,
                        price = price
                    ) { }
                } else { // to make a rectangular product card
                    RectangularProductCardHomePage(
                        productName = prod.key,
                        price = price
                    ) { }
                }
            }
        }
    }
}