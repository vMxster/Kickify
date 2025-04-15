package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.kickify.R
import it.unibo.kickify.data.models.ShopCategory
import it.unibo.kickify.ui.theme.Black
import it.unibo.kickify.ui.theme.GhostWhite

@Composable
fun HomeScreenSmallBrandLogos(brandName: Int){
    Image(
        painter = painterResource(brandName),
        "",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(GhostWhite)
            .padding(4.dp)
    )
    Spacer(Modifier.width(11.dp))
}

@Composable
fun HomeScreenCategory(categoryName: ShopCategory, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.padding(horizontal = 4.dp),
        colors =  ButtonDefaults.buttonColors(
            containerColor = GhostWhite
        )
    ) {
        Text(categoryName.toString(),
            color = Black)
    }
}

@Composable
fun HomeScreenSection(
    sectionTitle:String,
    prodList: List<String>
    /*onClick: () -> Unit*/
){
    Column (
        modifier = Modifier.fillMaxWidth(),
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
                SquareProductCardHomePage(
                    productName = prod,
                    price = price
                ) { }
            }
        }
    }
}