package it.unibo.kickify.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import it.unibo.kickify.data.models.PaymentMethods
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.OrderCardContainer
import it.unibo.kickify.ui.composables.OrderItem
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.products.ProductsViewModel
import it.unibo.kickify.ui.screens.settings.SettingsViewModel

@Composable
fun MyOrdersScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    ordersViewModel: OrdersViewModel,
    achievementsViewModel: AchievementsViewModel,
    productsViewModel: ProductsViewModel
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val userEmail by settingsViewModel.userId.collectAsStateWithLifecycle()
    val orders by ordersViewModel.orders.collectAsStateWithLifecycle()
    val orderDetails by ordersViewModel.ordersWithProducts.collectAsStateWithLifecycle()
    val errorMessage by ordersViewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by ordersViewModel.isLoading.collectAsStateWithLifecycle()

    val productList by productsViewModel.products.collectAsStateWithLifecycle()
    val orderInfo = orderDetails.groupBy { it.orderId }

    LaunchedEffect(userEmail) {
        ordersViewModel.getOrders(userEmail.lowercase())
        ordersViewModel.getOrdersWithProducts(userEmail.lowercase())
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.myordersScreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading,
        achievementsViewModel = achievementsViewModel
    ) {
        // show error if present
        LaunchedEffect(errorMessage) {
            errorMessage?.let { message ->
                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Long
                )
            }
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            if (orders.isEmpty()) {
                item {
                    Text(stringResource(R.string.noOrdersFound))
                }
            }

            items(orders){ orderDetails ->
                OrderCardContainer(
                    orderID = "${orderDetails.orderId}",
                    orderDate = ordersViewModel.convertDateFormat(orderDetails.orderDate) ?: "",
                    paymentMethod = PaymentMethods.getFromString(orderDetails.paymentMethod)
                        ?: PaymentMethods.PAYPAL,
                    totalPrice = orderDetails.totalCost.toFloat(),
                    actionDetailsButton = {
                        navController.navigate(
                            KickifyRoute.OrderDetails(orderDetails.orderId)
                        )
                    }
                ) {
                    val info = orderInfo[orderDetails.orderId] ?: listOf()
                    info.forEach { item ->
                        val imgMap = productList.getOrNull() ?: emptyMap()
                        val img = imgMap.entries.first {
                            it.key.productId == item.productId && it.value.number == 1
                        }
                        OrderItem(
                            imageUrl = img.value.url,
                            productName = item.name,
                            productSize = item.size.toString(),
                            qty = item.quantity,
                            colorString = item.color,
                            finalPrice = item.price.toFloat(),
                        )
                    }
                }
            }
        }
    }
}