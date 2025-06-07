package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
fun HomeScreenSmallBrandLogos(name: String, brandResLogoID: Int, onClick: (String) -> Unit) {
    Image(
        painter = painterResource(brandResLogoID),
        "",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .clickable { onClick(name) } // To Add the Brand Name
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
    )
    Spacer(Modifier.width(14.dp))
}

@Composable
fun HomeScreenBrandsSection(
    brands: Map<String, Int>,
    onClickAction: (String) -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for ((name, id) in brands.entries) {
            val brand = stringResource(R.string.brand)
            HomeScreenSmallBrandLogos(
                name = name,
                brandResLogoID = id,
                onClick = { onClickAction("$brand: $name") }
            )
        }
    }
}

@Composable
fun HomeScreenCategorySection(onClick: (String) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp).padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShopCategory.entries.forEach {
            HomeScreenCategoryButton(
                it, onClick = { category -> onClick(category) }
            )
        }
    }
}

@Composable
fun HomeScreenCategoryButton(categoryName: ShopCategory, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(categoryName.toString()) },
        modifier = Modifier.padding(horizontal = 4.dp),
        colors =  ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text( getHomeScreenCategoryString(categoryName) )
    }
}

@Composable
private fun getHomeScreenCategoryString(category: ShopCategory): String {
    return when(category){
        ShopCategory.Men -> stringResource(R.string.shopCategory_men)
        ShopCategory.Women -> stringResource(R.string.shopCategory_women)
        ShopCategory.Kids -> stringResource(R.string.shopCategory_kids)
    }
}

@Composable
fun HomeScreenShoesSectionHeader(
    sectionTitle: String,
    onClickButtonAction: () -> Unit,
    modifier: Modifier = Modifier
){
    Row (
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = { onClickButtonAction() }) {
            Text(stringResource(R.string.homescreen_seeAll))
        }
    }
}