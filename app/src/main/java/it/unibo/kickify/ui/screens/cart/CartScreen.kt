package it.unibo.kickify.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.CartAndCheckoutResume
import it.unibo.kickify.ui.composables.CartItem
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.products.ProductsViewModel

@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    productsViewModel: ProductsViewModel,
    achievementsViewModel: AchievementsViewModel
){
    val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    val subTotal by cartViewModel.subTotal.collectAsStateWithLifecycle()
    val total by cartViewModel.total.collectAsStateWithLifecycle()
    val shippingCost by cartViewModel.shippingCost.collectAsStateWithLifecycle()
    val errorMessage by cartViewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by cartViewModel.isLoading.collectAsStateWithLifecycle()
    val productImages by productsViewModel.products.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    // show error if present
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
        }
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.cartscreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading,
        snackBarHostState = snackBarHostState,
        achievementsViewModel = achievementsViewModel
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if(cartItems.isEmpty()){
                Text(stringResource(R.string.emptyCart))
            } else {
                cartItems.forEach { item ->
                    var imgUrl = ""
                    productImages.onSuccess { l ->
                        val img = l.find { it.first.productId == item.cartProduct.productId }
                        imgUrl = img?.second?.url ?: ""
                    }
                    CartItem(
                        itemName = item.nome,
                        price = item.prezzo,
                        size = item.cartProduct.size.toInt(),
                        productColor = item.cartProduct.color,
                        imageUrl = imgUrl,
                        quantity = item.cartProduct.quantity,
                        onChangeQuantity = { newQty ->
                            cartViewModel.updateQuantity(
                                productId = item.cartProduct.productId,
                                color = item.cartProduct.color,
                                size = item.cartProduct.size,
                                newQuantity = newQty
                            )
                        },
                        onDelete = {
                            cartViewModel.removeFromCart(
                                productId = item.cartProduct.productId,
                                color = item.cartProduct.color,
                                size = item.cartProduct.size
                            )
                        }
                    )
                    Spacer(Modifier.height(15.dp))
                }
            }
            Spacer(Modifier.height(20.dp))

            CartAndCheckoutResume(
                subTotal = subTotal,
                shipping = shippingCost,
                total = total,
                onButtonClickAction = { navController.navigate(KickifyRoute.Checkout) }
            )
        }
    }
}