package it.unibo.kickify.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel,
    achievementsViewModel: AchievementsViewModel,
    title: String? = null
) {
    val titleString = title ?: stringResource(R.string.exploreShoes)

    val productList by productsViewModel.products.collectAsStateWithLifecycle()
    val popularProducts by productsViewModel.popularProducts.collectAsStateWithLifecycle()
    val newProducts by productsViewModel.newProducts.collectAsStateWithLifecycle()
    val discountedProducts by productsViewModel.discountedProducts.collectAsStateWithLifecycle()
    val filteredProducts by productsViewModel.filteredProducts.collectAsStateWithLifecycle()
    val filterState by productsViewModel.filterState.collectAsStateWithLifecycle()
    val isLoading by productsViewModel.isLoading.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    var showFilterSheet by remember { mutableStateOf(false) }

    val displayProducts = when {
        filterState != FilterState() -> filteredProducts
        titleString == stringResource(R.string.homescreen_popular) -> {
            val popularList = popularProducts.getOrNull() ?: emptyList()
            Result.success(productList.getOrNull()?.filter { entry ->
                popularList.contains(entry.key)
            } ?: emptyMap())
        }
        titleString == stringResource(R.string.homescreen_novelties) -> {
            val newList = newProducts.getOrNull() ?: emptyList()
            Result.success(productList.getOrNull()?.filter { entry ->
                newList.contains(entry.key)
            } ?: emptyMap())
        }
        titleString == stringResource(R.string.homescreen_discounted) -> {
            val discountedList = discountedProducts.getOrNull() ?: emptyList()
            Result.success(productList.getOrNull()?.filter { entry ->
                discountedList.contains(entry.key)
            } ?: emptyMap())
        }
        else -> productList
    }

    ScreenTemplate(
        screenTitle = titleString,
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading,
        achievementsViewModel = achievementsViewModel,
        onApplyFilter = { newFilterState ->
            productsViewModel.updateFilters(newFilterState)
            showFilterSheet = false
        },
        onResetFilter = {
            productsViewModel.resetFilters()
        }
    ) {
        if (showFilterSheet) {
            FilterScreen(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                initialFilterState = filterState,
                onApplyFilter = { newFilterState ->
                    productsViewModel.updateFilters(newFilterState)
                    showFilterSheet = false
                },
                onResetFilter = {
                    productsViewModel.resetFilters()
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp).padding(top = 10.dp)
        ) {
            displayProducts.onSuccess { list ->
                if (list.isEmpty()) {
                    Text(stringResource(R.string.noPopularShoesError))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(list.entries.size) { index ->
                            val entry = list.entries.elementAt(index)
                            val prod = entry.key
                            val img = entry.value

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