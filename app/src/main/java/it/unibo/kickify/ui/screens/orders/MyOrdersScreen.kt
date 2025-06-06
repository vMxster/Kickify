package it.unibo.kickify.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import it.unibo.kickify.ui.composables.OrdersTitleLine
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.delay

@Composable
fun MyOrdersScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    ordersViewModel: OrdersViewModel
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val userEmail by settingsViewModel.userId.collectAsStateWithLifecycle()

    val orders by ordersViewModel.orders.collectAsStateWithLifecycle()
    val orderDetails by ordersViewModel.ordersWithProducts.collectAsStateWithLifecycle()
    val errorMessage by ordersViewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by ordersViewModel.isLoading.collectAsStateWithLifecycle()

    val completedOrders = orderDetails.filter { it.isDelivered }
    val ongoingOrders = orderDetails.filter { !it.isDelivered }

    ScreenTemplate(
        screenTitle = stringResource(R.string.myordersScreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading
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

        LaunchedEffect(Unit) {
            delay(500)
            ordersViewModel.getOrders(userEmail)
            ordersViewModel.getOrdersWithProducts(userEmail)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if(ongoingOrders.isEmpty() && completedOrders.isEmpty()){
                Text(stringResource(R.string.noOrdersFound))
            }

            if(ongoingOrders.isNotEmpty()) {
                OrdersTitleLine(stringResource(R.string.myordersScreen_ongoingOrders))
                ongoingOrders.forEach { orderDetails ->
                    OrderCardContainer(
                        orderID = "#${orderDetails.orderId}",
                        orderDate = orderDetails.orderDate,
                        paymentMethod = PaymentMethods.getFromString(orderDetails.paymentMethod)
                            ?: PaymentMethods.PAYPAL,
                        totalPrice = orderDetails.price.toFloat(),
                        actionDetailsButton = {
                            navController.navigate(
                                KickifyRoute.OrderDetails(
                                    orderDetails.orderId
                                )
                            )
                        }
                    ) {
                        OrderItem(
                            productName = "Adidas Spezial",
                            productSize = "44",
                            qty = 1,
                            colorString = "blue",
                            finalPrice = 80.0f,
                            originalPrice = 109.99f,
                        )
                        OrderItem(
                            productName = "New Balance 740",
                            productSize = "44",
                            qty = 1,
                            colorString = "blue",
                            finalPrice = 119.99f,
                        )
                    }
                }
            }

            if(completedOrders.isNotEmpty()) {
                OrdersTitleLine(stringResource(R.string.myordersScreen_completedOrders))
                OrderCardContainer(
                    orderID = "#278911819592", orderDate = "20-1-2025",
                    paymentMethod = PaymentMethods.MASTERCARD, totalPrice = 99.99f,
                    actionDetailsButton = {}
                ) {
                    OrderItem(
                        productName = "Nike Air",
                        productSize = "44",
                        qty = 1,
                        colorString = "blue",
                        finalPrice = 99.99f
                    )
                }
            }
        }
    }
}