package it.unibo.kickify.ui.screens.productList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.models.ShopCategory
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.ProductCardShoesPage

@Composable
fun ProductListScreen(
    navController: NavController,
    title: String? = null
){
    val itemNames = listOf("Nike Air Force", "Nike Air Max",
        "Nike Jordan", "Nike Air Max", "Nike Air Force", "Nike Air Max")
    val itemPrices = listOf(98.76, 99.89, 119.99, 189.99, 69.99, 189.99)
    val titleString = title ?: stringResource(R.string.homescreen_popular)

    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = titleString
            )
        },
        bottomBar = { }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(itemNames.size) { index ->
                    ProductCardShoesPage(
                        productName = itemNames[index],
                        price = itemPrices[index],
                        onClick = {},
                        category = ShopCategory.Men
                    )
                }
            }
        }
    }
}