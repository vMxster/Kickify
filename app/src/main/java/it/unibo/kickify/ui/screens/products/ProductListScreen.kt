package it.unibo.kickify.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.models.ShopCategory
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ProductCardShoesPage
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel

@Composable
fun ProductListScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel,
    achievementsViewModel: AchievementsViewModel,
    title: String? = null
) {
    val titleString = title ?: stringResource(R.string.allShoes)

    val productList by productsViewModel.products.collectAsStateWithLifecycle()
    val isLoading by productsViewModel.isLoading.collectAsStateWithLifecycle()

    ScreenTemplate(
        screenTitle = titleString,
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
        ) {
            productList.onSuccess { list ->
                if (list.isEmpty()) {
                    Text(stringResource(R.string.errorLoadingData))

                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(list.size) { index ->
                            val prod = list[index].first
                            val img = list[index].second

                            ProductCardShoesPage(
                                productName = "${prod.brand} ${prod.name}",
                                mainImgUrl = img.url,
                                price = prod.price,
                                category = ShopCategory.valueOf(prod.genre),
                                onClick = {
                                    navController.navigate(
                                        KickifyRoute.ProductDetails(prod.productId)
                                    )
                                }
                            )
                        }
                    }
                }
            }.onFailure { e ->
                Text(stringResource(R.string.errorLoadingData) +
                        "\n${e.message}")
            }
        }
    }
}