package it.unibo.kickify.ui.screens.wishlist

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ProductCardWishlistPage

@Composable
fun WishlistScreen(
    navController: NavController
){
    val itemNames = listOf("Nike Lunarglide", "Nike Zoom", "Nike Air Max", "Nike Zoom 2K")
    val itemPrices = listOf(59.99, 37.99, 59.99, 37.99)

    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.wishlist_title)
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ){
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(itemNames.size){ index ->
                    ProductCardWishlistPage(
                        productName = itemNames[index],
                        price = itemPrices[index],
                        onClick = {}
                    )
                }
            }
        }
    }
}