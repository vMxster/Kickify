package it.unibo.kickify.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import it.unibo.kickify.data.database.ReviewWithUserInfo
import it.unibo.kickify.data.database.Version
import it.unibo.kickify.ui.composables.ColorsList
import it.unibo.kickify.ui.composables.ProductDetailsFooter
import it.unibo.kickify.ui.composables.ProductImage
import it.unibo.kickify.ui.composables.ProductLongDescription
import it.unibo.kickify.ui.composables.ProductName
import it.unibo.kickify.ui.composables.ProductPhotoGallery
import it.unibo.kickify.ui.composables.ProductPrice
import it.unibo.kickify.ui.composables.RatingBar
import it.unibo.kickify.ui.composables.ReviewCard
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.composables.SectionTitle
import it.unibo.kickify.ui.composables.SizesList

@Composable
fun ProductDetailsScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel,
    productId: Int
) {
    val productDetails by productsViewModel.productDetails.collectAsStateWithLifecycle()
    val productReviews by productsViewModel.productReviews.collectAsStateWithLifecycle()
    val productList by productsViewModel.products.collectAsStateWithLifecycle()
    val productImages by productsViewModel.productImages.collectAsStateWithLifecycle()
    val isLoading by productsViewModel.isLoading.collectAsStateWithLifecycle()

    val list = productList.getOrNull() ?: emptyList()
    val prodInfo = list.firstOrNull { pair -> pair.first.productId == productId }
    val product = prodInfo?.first
    val img = prodInfo?.second

    var reviews: List<ReviewWithUserInfo> = listOf()
    var votes: List<Double> = listOf()
    var prodVersions: List<Version> = listOf()

    productReviews?.onSuccess { l ->
        reviews = l
        votes = reviews.map { r -> r.review.vote }
    }
    productDetails?.onSuccess { r -> prodVersions = r.versions }

    LaunchedEffect(Unit) {
        productsViewModel.getReviewsOfProduct(productId)
        productsViewModel.loadProductDetails(productId)
        productsViewModel.getProductImages(productId)
    }

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
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (list.isEmpty()) {
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
                    RatingBar(nrRatings = reviews.size, votes)
                }

                ProductLongDescription(longDescr = product?.desc ?: "")

                SectionTitle(title = stringResource(R.string.prodDetails_gallery))
                ProductPhotoGallery(
                    images = productImages,
                    productName = product?.name ?: ""
                )

                val colorAvailability: MutableMap<String, Boolean> = mutableMapOf()
                val sizesAvailability: MutableMap<Int, Boolean> = mutableMapOf()
                if(prodVersions.isNotEmpty()) {
                    prodVersions.forEach { v ->
                        sizesAvailability[v.size.toInt()] = v.quantity > 0
                        colorAvailability[v.color] = v.quantity > 0
                    }
                }
                SectionTitle(title = stringResource(R.string.size))
                SizesList(
                    sizeSelected = null,
                    sizesAvailability = sizesAvailability,
                    onSizeSelected = { }
                )

                SectionTitle(title = stringResource(R.string.color))
                ColorsList(
                    colorSelected = null,
                    colorAvailability = colorAvailability,
                    onColorSelected = { }
                )

                SectionTitle(title = stringResource(R.string.prodDetails_reviews))
                if(reviews.isNotEmpty()){
                    reviews.forEach { r ->
                        ReviewCard(r)
                    }
                } else {
                    Text(stringResource(R.string.prodDetails_noReviewsFound))
                }
            }
        }
    }
}