package it.unibo.kickify.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.AddressOnMapBox
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.OrderIDCenterTitle
import it.unibo.kickify.ui.composables.OrderItem
import it.unibo.kickify.ui.composables.OrdersTitleLine
import it.unibo.kickify.ui.composables.StepProgressBar

@Composable
fun OrderDetailsScreen(
    navController: NavController,
    orderID: String
) {
    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.trackOrder)
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            OrderIDCenterTitle(orderID)

            OrderItem(
                productName = "Adidas Spezial",
                productSize = "44",
                qty = 1,
                colorString = "blue",
                finalPrice = 80.0f,
                originalPrice = 109.99f,
            )
            HorizontalDivider(thickness = 3.dp, modifier = Modifier.padding(vertical = 6.dp))

            OrdersTitleLine(stringResource(R.string.orderDetails))
            Row (
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.expectedDeliveryDate))
                Text(text = "03 Sep 2024")
            }
            Row (
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.trackingID))
                Text(text = "TRK452126542")
            }
            HorizontalDivider(thickness = 3.dp, modifier = Modifier.padding(vertical = 6.dp))

            OrdersTitleLine(stringResource(R.string.orderStatus))

            StepProgressBar(
                steps = listOf(
                    stringResource(R.string.orderPlaced) + "\n" + "23 Aug 2024, 04:25 PM",
                    stringResource(R.string.inProgress) + "\n" + "23 Aug 2024, 05:25 PM",
                    stringResource(R.string.shipped) + "\n" + "Expected 24 Aug 2024",
                    stringResource(R.string.delivered) + "\n" + "Expected 24 Aug 2024"
                ),
                currentStep = 1
            )

            OrdersTitleLine(stringResource(R.string.parceLocation))

            AddressOnMapBox("Via Roma 123, Cesena, 47521, Italia")
        }
    }
}