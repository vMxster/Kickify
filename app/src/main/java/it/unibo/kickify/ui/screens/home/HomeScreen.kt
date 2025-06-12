package it.unibo.kickify.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.database.Product
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.HomeScreenBrandsSection
import it.unibo.kickify.ui.composables.HomeScreenCategorySection
import it.unibo.kickify.ui.composables.HomeScreenShoesSectionHeader
import it.unibo.kickify.ui.composables.RectangularProductCardHomePage
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.composables.SearchRoundedTextField
import it.unibo.kickify.ui.composables.SquareProductCardHomePage
import it.unibo.kickify.ui.screens.products.ProductsViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel
){
    val isLoading by productsViewModel.isLoading.collectAsStateWithLifecycle()

    val popularProducts by productsViewModel.popularProducts.collectAsStateWithLifecycle()
    val newProducts by productsViewModel.newProducts.collectAsStateWithLifecycle()
    val discountedProducts by productsViewModel.discountedProducts.collectAsStateWithLifecycle()

    val brands = mapOf(
        "Adidas" to R.drawable.adidas,
        "Nike" to R.drawable.nike,
        "Puma" to R.drawable.puma)

    var populars : List<Product> = listOf()
    var newProds : List<Product> = listOf()
    var discounted : List<Product> = listOf()

    LaunchedEffect(popularProducts, newProducts, discountedProducts) {
        popularProducts.onSuccess { populars = it }
            .onFailure { println("popular prod error ${it.message}") }

        newProducts.onSuccess { newProds = it }
            .onFailure { println("new prod error ${it.message}") }

        discountedProducts.onSuccess { discounted = it }
            .onFailure { println("discount prod error ${it.message}") }
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.app_name),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading
    ) {

        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            columns = GridCells.Fixed(2)
        ) {
            item(span = { GridItemSpan(2) }) {
                SearchRoundedTextField(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    onSearch = { query ->
                        productsViewModel.searchProducts(query)
                        navController.navigate(KickifyRoute.ProductList)
                    }
                )
            }
            item(span = { GridItemSpan(2) }) {
                HomeScreenCategorySection(
                    onClick = { category ->
                        navController.navigate(KickifyRoute.ProductListWithCategory(category))
                    }
                )
            }
            item(span = { GridItemSpan(2) }) {
                HomeScreenBrandsSection(
                    brands,
                    onClickAction = { brand ->
                        navController.navigate(KickifyRoute.ProductListWithCategory(brand))
                    }
                )
            }

            // begin shoes section
            item(span = { GridItemSpan(2) }) {
                val categ = stringResource(R.string.homescreen_popular)
                HomeScreenShoesSectionHeader(
                    sectionTitle = categ,
                    onClickButtonAction = {
                        navController.navigate(KickifyRoute.ProductListWithCategory(categ))
                    }
                )
            }
            items(populars.take(2)) { prod ->
                SquareProductCardHomePage(
                    productID = prod.productId,
                    productName = "${prod.brand} ${prod.name}",
                    mainImgUrl = "",
                    price = prod.price,
                    onClick = {
                        navController.navigate(KickifyRoute.ProductDetails(prod.productId))
                    }
                )
            }

            val newProd = newProds.firstOrNull()

            item(span = { GridItemSpan(2) }) {
                val categ = stringResource(R.string.homescreen_novelties)
                HomeScreenShoesSectionHeader(
                    sectionTitle = categ,
                    onClickButtonAction = {
                        navController.navigate(KickifyRoute.ProductListWithCategory(categ))
                    }
                )
            }
            item(span = { GridItemSpan(2) }) {
                if (newProd != null) {
                    RectangularProductCardHomePage(
                        productID = newProd.productId,
                        productName = "${newProd.brand} ${newProd.name}",
                        mainImgUrl = "",
                        price = newProd.price,
                        onClick = {
                            navController.navigate(KickifyRoute.ProductDetails(newProd.productId))
                        }
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                val categ = stringResource(R.string.homescreen_discounted)
                HomeScreenShoesSectionHeader(
                    sectionTitle = categ,
                    onClickButtonAction = {
                        navController.navigate(KickifyRoute.ProductListWithCategory(categ))
                    }
                )
            }
            items(discounted.take(2)) { prod ->
                SquareProductCardHomePage(
                    productID = prod.productId,
                    productName = "${prod.brand} ${prod.name}",
                    mainImgUrl = "",
                    price = prod.price,
                    onClick = {
                        navController.navigate(KickifyRoute.ProductDetails(prod.productId))
                    }
                )
            }
        }
    }
}