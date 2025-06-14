package it.unibo.kickify.ui.screens.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import it.unibo.kickify.R
import it.unibo.kickify.data.database.ReviewWithUserInfo
import it.unibo.kickify.data.database.Version
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ColorsList
import it.unibo.kickify.ui.composables.FullscreenImageDialog
import it.unibo.kickify.ui.composables.ProductDetailsFooter
import it.unibo.kickify.ui.composables.ProductLongDescription
import it.unibo.kickify.ui.composables.ProductName
import it.unibo.kickify.ui.composables.ProductPhotoGallery
import it.unibo.kickify.ui.composables.ProductPrice
import it.unibo.kickify.ui.composables.RatingBar
import it.unibo.kickify.ui.composables.ReviewCard
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.composables.SectionTitle
import it.unibo.kickify.ui.composables.SizesList
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.cart.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    navController: NavController,
    productsViewModel: ProductsViewModel,
    cartViewModel: CartViewModel,
    achievementsViewModel: AchievementsViewModel,
    productId: Int
) {
    val productDetails by productsViewModel.productDetails.collectAsStateWithLifecycle()
    val productReviews by productsViewModel.productReviews.collectAsStateWithLifecycle()
    val productList by productsViewModel.products.collectAsStateWithLifecycle()
    val productImages by productsViewModel.productImages.collectAsStateWithLifecycle()
    val isLoading by productsViewModel.isLoading.collectAsStateWithLifecycle()
    val email by cartViewModel.email.collectAsStateWithLifecycle()

    val list = productList.getOrNull() ?: emptyMap()
    val prodInfo = list.entries.firstOrNull { entry -> entry.key.productId == productId }
    val product = prodInfo?.key
    val isInWishlist by productsViewModel.isInWishlist.collectAsStateWithLifecycle()
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
        productsViewModel.isInWishlist(productId)
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.details),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading,
        achievementsViewModel = achievementsViewModel,
        isInWishlist = isInWishlist,
        onToggleWishlist = { productsViewModel.toggleWishlist(productId) }
    ) {
        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }
        var showFullscreenImage by remember { mutableStateOf(false) }

        val mainImg = productImages.find { it.number == mainImageIndex }

        if(showFullscreenImage){
            FullscreenImageDialog(
                imgUrl = mainImg?.url ?: "",
                productName = product?.name ?: "",
                onDismissRequest = { showFullscreenImage = false }
            )
        }
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
                AsyncImage(
                    model = mainImg?.url ?: "",
                    contentDescription = product?.name ?: "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                        .clickable { showFullscreenImage = true }
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

                ProductDetailsFooter(product?.price ?: 0.0,
                    addProductToCartAction = {
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

                SectionTitle(
                    title = stringResource(R.string.prodDetails_reviews),
                    buttonIcon = Icons.Outlined.Add,
                    iconDescription = stringResource(R.string.prodDetails_addReview),
                    onButtonClick = { showBottomSheet = true }
                )
                if(reviews.isNotEmpty()){
                    reviews.forEach { r ->
                        ReviewCard(r,
                            // user can delete only their reviews
                            showDeleteButton = r.review.email == email,
                            deleteReviewAction = {
                                productsViewModel.deleteReviewOfProduct(email, productId)
                            }
                        )
                    }
                } else {
                    Text(stringResource(R.string.prodDetails_noReviewsFound))
                }
                Spacer(Modifier.height(10.dp))

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState
                    ) {
                        val ctx = LocalContext.current
                        val focusManager = LocalFocusManager.current

                        var comment by rememberSaveable { mutableStateOf("") }
                        var isErrorComment by remember { mutableStateOf(false) }
                        var errorMessageComment by remember { mutableStateOf("") }
                        val commentFocusRequester = remember { FocusRequester() }

                        var rating by rememberSaveable { mutableStateOf("") }
                        var isErrorRating by remember { mutableStateOf(false) }
                        var errorMessageRating by remember { mutableStateOf("") }
                        val ratingFocusRequester = remember { FocusRequester() }

                        LaunchedEffect(Unit) {
                            ratingFocusRequester.requestFocus()
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                .padding(horizontal = 10.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val reviewModifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 24.dp)

                            Text(
                                text = stringResource(R.string.prodDetails_addReview),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = reviewModifier
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = stringResource(R.string.prodDetails_rating),
                                modifier = reviewModifier
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = rating,
                                onValueChange = { newValue ->
                                    val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                                    val parts = filteredValue.split('.')
                                    rating = if (parts.size <= 2) {
                                        filteredValue
                                    } else {
                                        parts[0] + "." + parts[1]
                                    }
                                },
                                isError = isErrorRating,
                                supportingText = { Text(errorMessageRating)},
                                placeholder = { Text(stringResource(R.string.prodDetails_typeinValidRating)) },
                                shape = RoundedCornerShape(16.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        if(rating.isNotEmpty()){
                                            val number = rating.toDoubleOrNull()
                                            if (number == null || number < 0.0 || number > 5.0) {
                                                isErrorRating = true
                                                errorMessageRating = ctx.getString(R.string.prodDetails_typeinValidRating)
                                            } else {
                                                isErrorRating = false
                                                errorMessageRating = ""
                                                commentFocusRequester.requestFocus()
                                            }
                                        } else {
                                            isErrorRating = true
                                            errorMessageRating = ctx.getString(R.string.prodDetails_typeinValidRating)
                                        }
                                    }
                                ),
                                modifier = reviewModifier.focusRequester(ratingFocusRequester)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = stringResource(R.string.prodDetails_comment),
                                modifier = reviewModifier
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = comment,
                                onValueChange = {
                                    isErrorComment = false
                                    comment = it
                                },
                                minLines = 3,
                                singleLine = false,
                                placeholder = { Text(stringResource(R.string.prodDetails_comment)) },
                                shape = RoundedCornerShape(16.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if(comment.isNotBlank()) {
                                            isErrorComment = false
                                            focusManager.clearFocus()
                                        } else {
                                            isErrorComment = true
                                            errorMessageComment = ctx.getString(R.string.prodDetails_typeInComment)
                                        }
                                    }
                                ),
                                isError = isErrorComment,
                                supportingText = { Text(errorMessageComment)},
                                modifier = reviewModifier.focusRequester(commentFocusRequester)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                modifier = reviewModifier,
                                onClick = {
                                    showBottomSheet = false
                                    if(comment.isEmpty() || comment.isBlank()){
                                        isErrorComment = true
                                        errorMessageComment = ctx.getString(R.string.prodDetails_typeInComment)
                                    }
                                    if(rating.toDouble() <= 0.0 || rating.toDouble() >= 5.0){
                                        isErrorRating = true
                                        errorMessageRating = ctx.getString(R.string.prodDetails_typeinValidRating)
                                    }
                                    if(comment.isNotBlank() && (rating.toDouble() in 0.0..5.0)) {
                                        achievementsViewModel.achieveAchievement(2)
                                        productsViewModel.addReviewOfProduct(
                                            email, productId, rating.toDouble(), comment
                                        )
                                    }
                                }
                            ) {
                                Text(stringResource(R.string.prodDetails_addReview))
                            }
                        }
                    }
                }
            }
        }
    }
}