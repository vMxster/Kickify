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
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.products.ProductsViewModel
import it.unibo.kickify.ui.screens.settings.SettingsViewModel

@Composable
fun WishlistScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    wishlistViewModel: WishlistViewModel,
    productsViewModel: ProductsViewModel,
    achievementsViewModel: AchievementsViewModel
){
    val isLoading by wishlistViewModel.isLoading.collectAsStateWithLifecycle()
    val wishlistState by wishlistViewModel.wishlistState.collectAsStateWithLifecycle()
    val products by productsViewModel.products.collectAsStateWithLifecycle()

    val email by settingsViewModel.userId.collectAsStateWithLifecycle()

    LaunchedEffect(wishlistState, email, products) {
        wishlistViewModel.fetchWishlist(email)
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.wishlist_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading,
        achievementsViewModel = achievementsViewModel
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp).padding(top = 10.dp)
        ){
            val prodList = products.getOrNull() ?: emptyList()

            if (wishlistState.isEmpty()) {
                Text(stringResource(R.string.emptyWishlist))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(wishlistState.size) { index ->
                        val prodID = wishlistState[index].productId
                        prodList.firstOrNull { it.first.productId == prodID }.let {
                            val prodInfo = it?.first
                            val prodImg = it?.second

                            ProductCardWishlistPage(
                                mainImgUrl = prodImg?.url ?: "",
                                productName = "${prodInfo?.brand} ${prodInfo?.name}",
                                price = prodInfo?.price ?: 0.0,
                                onClick = {
                                    navController.navigate(KickifyRoute.ProductDetails(prodID))
                                },
                                onToggleWishlistIcon = { checked ->
                                    if (!checked) {
                                        wishlistViewModel.removeFromWishlist(email, prodID)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}