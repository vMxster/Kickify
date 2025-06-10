package it.unibo.kickify.ui.screens.productDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.ColorsList
import it.unibo.kickify.ui.composables.ProductDetailsFooter
import it.unibo.kickify.ui.composables.ProductImage
import it.unibo.kickify.ui.composables.ProductLongDescription
import it.unibo.kickify.ui.composables.ProductName
import it.unibo.kickify.ui.composables.ProductPhotoGallery
import it.unibo.kickify.ui.composables.ProductPrice
import it.unibo.kickify.ui.composables.RatingBar
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.composables.SectionTitle
import it.unibo.kickify.ui.composables.SizesList
import it.unibo.kickify.ui.screens.productList.ProductsViewModel

@Composable
fun ProductDetailsScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel,
    productId: Int
) {
    val productList by productsViewModel.products.collectAsStateWithLifecycle()
    val isLoading by productsViewModel.isLoading.collectAsStateWithLifecycle()

    val list = productList.getOrNull() ?: emptyList()
    val prodInfo = list.firstOrNull { pair -> pair.first.productId == productId }
    val product = prodInfo?.first
    val img = prodInfo?.second

    ScreenTemplate(
        screenTitle = stringResource(R.string.details),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { ProductDetailsFooter(product?.price ?: 0.0) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (isLoading) {
                CircularProgressIndicator()

            } else if (list.isEmpty()) {
                Text(stringResource(R.string.errorLoadingData))

            } else {
                ProductImage(
                    imgUrl = img?.url ?: "",
                    productName = product?.name ?: "",
                    modifier = Modifier.fillMaxWidth()
                )

                ProductName(
                    brand = product?.brand ?: "",
                    name = product?.name ?: "",
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProductPrice(product?.price ?: 0.0)
                    RatingBar(nrRatings = 4, listOf(5.0, 5.0, 5.0, 5.0))
                }

                ProductLongDescription(longDescr = product?.desc ?: "")

                SectionTitle(title = stringResource(R.string.prodDetails_gallery))
                ProductPhotoGallery(
                    images = listOf(
                        R.drawable.nike_air_zoom_1,
                        R.drawable.nike_air_zoom_2, R.drawable.nike_air_zoom_3
                    ),
                    productName = product?.name ?: ""
                )

                SectionTitle(title = stringResource(R.string.size))
                SizesList(
                    sizeSelected = 40,
                    sizesAvailability = mapOf(
                        38 to true, 39 to true, 40 to true, 41 to true,
                        42 to false, 43 to true, 44 to true, 45 to true
                    ),
                    onSizeSelected = { }
                )

                SectionTitle(title = stringResource(R.string.color))
                ColorsList(
                    colorSelected = Color.White,
                    colorAvailability = mapOf(
                        Color.White to true, Color.Black to false,
                        Color.Red to true, Color.Blue to true,
                        Color.Yellow to false
                    ),
                    onColorSelected = { }
                )
            }
        }
    }
}