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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import it.unibo.kickify.ui.screens.cart.CartViewModel

@Composable
fun ProductDetailsScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel,
    cartViewModel: CartViewModel,
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
    var mainImageIndex by remember { mutableIntStateOf(1) }

    var reviews: List<ReviewWithUserInfo> = listOf()
    var votes: List<Double> = listOf()
    var prodVersions: List<Version> = listOf()

    productReviews?.onSuccess { l ->
        reviews = l
        votes = reviews.map { r -> r.review.vote }
    }
    productDetails?.onSuccess { r -> prodVersions = r.versions }

    var selectedSize by remember { mutableStateOf<Int?>(null) }
    var selectedColor by remember { mutableStateOf<Color?>(null) }

    LaunchedEffect(Unit) {
        productsViewModel.getReviewsOfProduct(productId)
        productsViewModel.loadProductDetails(productId)
        productsViewModel.getProductImages(productId)
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.details),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = {
            ProductDetailsFooter(product?.price ?: 0.0,
                addProductToCart = {
                    if(selectedColor != null && selectedSize != null) {
                        cartViewModel.updateQuantity(
                            productId = productId,
                            color = selectedColor.toString(),
                            size = selectedSize!!.toDouble(),
                            newQuantity = 1
                        )
                    }
                }
            )
        },
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
                val mainImg = productImages.find { it.number == mainImageIndex }
                ProductImage(
                    imgUrl = mainImg?.url ?: "",
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
                    productName = product?.name ?: "",
                    clickOnImageAction = { index -> mainImageIndex = index }
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
                    sizesAvailability = sizesAvailability.toSortedMap(),
                    onSizeSelected = { size -> selectedSize = size }
                )

                SectionTitle(title = stringResource(R.string.color))
                ColorsList(
                    colorSelected = null,
                    colorAvailability = colorAvailability.toSortedMap(),
                    onColorSelected = { color -> selectedColor = color }
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