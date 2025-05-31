package it.unibo.kickify.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.models.PaymentMethod
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.OrderCardContainer
import it.unibo.kickify.ui.composables.OrderItem
import it.unibo.kickify.ui.composables.OrdersTitleLine
import it.unibo.kickify.ui.composables.ScreenTemplate

@Composable
fun MyOrdersScreen(
    navController: NavController
) {
    ScreenTemplate(
        screenTitle = stringResource(R.string.myordersScreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
    ) {
        val scrollState = rememberScrollState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 10.dp)
                .verticalScroll(scrollState)
        ) {
            OrdersTitleLine(stringResource(R.string.myordersScreen_ongoingOrders))
            OrderCardContainer( navController,
                orderID = "#278917498174", orderDate = "01-05-2025",
                paymentMethod = PaymentMethod.PAYPAL, totalPrice = 184.0f
            ){
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

            OrdersTitleLine(stringResource(R.string.myordersScreen_completedOrders))
            OrderCardContainer(
                navController,
                orderID = "#278911819592", orderDate = "20-1-2025",
                paymentMethod = PaymentMethod.MASTERCARD, totalPrice = 99.99f
            ){
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