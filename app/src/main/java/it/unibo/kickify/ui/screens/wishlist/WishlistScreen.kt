package it.unibo.kickify.ui.screens.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ProductCardWishlistPage
import it.unibo.kickify.ui.composables.ScreenTemplate

@Composable
fun WishlistScreen(
    navController: NavController,
    wishlistViewModel: WishlistViewModel
){
    val wishlistState by wishlistViewModel.wishlistState.collectAsStateWithLifecycle()
    val email = "test@email.com"

    LaunchedEffect(wishlistState) {
        wishlistViewModel.fetchWishlist(email)
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.wishlist_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ){
            if (wishlistState.isSuccess) {
                val products = wishlistState.getOrNull() ?: emptyList()

                if(products.isEmpty()){
                    Text(stringResource(R.string.emptyWishlist))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(products.size) { index ->
                            val prodInfo = products[index].product
                            ProductCardWishlistPage(
                                mainImgUrl = "",
                                productName = "${prodInfo.brand} ${prodInfo.name}",
                                price = prodInfo.price,
                                onClick = {
                                    navController.navigate(KickifyRoute.ProductDetails(
                                        prodInfo.productId))
                                },
                                onToggleWishlistIcon = { checked ->
                                    if(!checked){
                                        wishlistViewModel.removeFromWishlist(
                                            email, prodInfo.productId
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                Text(stringResource(R.string.errorLoadingWishlist))
            }
        }
    }
}