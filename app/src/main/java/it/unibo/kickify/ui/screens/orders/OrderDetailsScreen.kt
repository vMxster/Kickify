package it.unibo.kickify.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import it.unibo.kickify.ui.composables.AddressOnMapBox
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.OrderInfo
import it.unibo.kickify.ui.composables.OrderItem
import it.unibo.kickify.ui.composables.OrdersTitleLine
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.composables.StepProgressBar
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.products.ProductsViewModel
import it.unibo.kickify.ui.screens.settings.SettingsViewModel

@Composable
fun OrderDetailsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    ordersViewModel: OrdersViewModel,
    achievementsViewModel: AchievementsViewModel,
    productsViewModel: ProductsViewModel,
    orderID: String
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val userEmail by settingsViewModel.userId.collectAsStateWithLifecycle()
    val orders by ordersViewModel.orders.collectAsStateWithLifecycle()
    val orderDetails by ordersViewModel.ordersWithProducts.collectAsStateWithLifecycle()
    val errorMessage by ordersViewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by ordersViewModel.isLoading.collectAsStateWithLifecycle()

    val productList by productsViewModel.products.collectAsStateWithLifecycle()
    val orderInfo = orderDetails.groupBy { it.orderId }
    val ordersTrackingMap by ordersViewModel.ordersTracking.collectAsStateWithLifecycle()
    val currentOrderTracking = ordersTrackingMap[orderID.toInt()]

    LaunchedEffect(userEmail) {
        ordersViewModel.getOrderTracking(orderID.toInt())
        ordersViewModel.getOrdersWithProducts(userEmail.lowercase())
    }

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
        screenTitle = stringResource(R.string.trackOrder),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        achievementsViewModel = achievementsViewModel,
        showLoadingOverlay = isLoading
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OrderInfo(orderID, orderDate = currentOrderTracking?.orderInfo?.orderDate ?: "")

            val orderDet = orders.find { it.orderId == orderID.toInt() }
            if(orderDet != null) {
                val info = orderInfo[orderDet.orderId] ?: listOf()

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

            HorizontalDivider(thickness = 3.dp, modifier = Modifier.padding(vertical = 6.dp))

            // Modifica per la barra di progressione
            val orderPlacedInfo = currentOrderTracking?.trackingStates?.find { it.status == "Placed" }
            val inProgressInfo = currentOrderTracking?.trackingStates?.find { it.status == "In progress" }
            val shippedInfo = currentOrderTracking?.trackingStates?.find { it.status == "Shipped" }
            val deliveredInfo = currentOrderTracking?.trackingStates?.find { it.status == "Delivered" }

            OrdersTitleLine(stringResource(R.string.orderDetails))
            Row (
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.expectedDeliveryDate))
                Text(text = deliveredInfo?.estimatedArrival ?: stringResource(R.string.notAvailable))
            }
            Row (
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.trackingID))
                Text(text = currentOrderTracking?.let { "TR-$orderID" } ?: stringResource(R.string.notAvailable))
            }
            HorizontalDivider(thickness = 3.dp, modifier = Modifier.padding(vertical = 6.dp))

            OrdersTitleLine(stringResource(R.string.orderStatus))

            // Determina lo stato attuale dell'ordine
            val values = listOf(orderPlacedInfo?.actualArrival, inProgressInfo?.actualArrival,
                shippedInfo?.actualArrival, deliveredInfo?.actualArrival)
            // step progress bar index is 0-based
            val currentStepIndex = values.mapIndexed { _, s ->  s != "null" }.count { it } -1

            StepProgressBar(
                steps = listOf(
                    stringResource(R.string.orderPlaced) + "\n" + displayEstimatedOrActualDateTime(orderPlacedInfo?.estimatedArrival, orderPlacedInfo?.actualArrival),
                    stringResource(R.string.inProgress) + "\n" + displayEstimatedOrActualDateTime(inProgressInfo?.estimatedArrival, inProgressInfo?.actualArrival),
                    stringResource(R.string.shipped) + "\n" + displayEstimatedOrActualDateTime(shippedInfo?.estimatedArrival, shippedInfo?.actualArrival),
                    stringResource(R.string.delivered) + "\n" + displayEstimatedOrActualDateTime(deliveredInfo?.estimatedArrival, deliveredInfo?.actualArrival)
                ),
                currentStep = currentStepIndex
            )

            // Modifica per la posizione del pacco
            OrdersTitleLine(stringResource(R.string.parcelLocation))
            AddressOnMapBox(currentOrderTracking?.orderInfo?.currentLocation ?: "",
                zoomLevel = 18.0, showAddressLabelIfAvailable = true)
        }
    }
}

@Composable
fun displayEstimatedOrActualDateTime(estimated: String?, actual: String?): String{
    if(actual != "null" && estimated != "null"){
        return "$actual"
    }
    if(actual == "null" && estimated != "null"){
        return stringResource(R.string.expected) + ": $estimated"
    }
    return stringResource(R.string.notAvailable)
}