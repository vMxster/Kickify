package it.unibo.kickify.ui.screens.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.CartAndCheckoutResume
import it.unibo.kickify.ui.composables.InformationCard

@Composable
fun CheckOutScreen(
    navController: NavController
){
    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.checkoutScreen_title)
            )
        },
        bottomBar = { }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 10.dp)
                .padding(vertical = 8.dp)
                .fillMaxSize()
        ) {

            InformationCard(
                emailAddress = "mario.rossi@gmail.com",
                phoneNr = "+39 1234567890",
                shippingAddress = "Via Roma 123, Cesena, 47521 - IT",
                payMethod = "paypal",
                paymentDetails = "mario.rossi@gmail.com"
            )
            CartAndCheckoutResume(
                subTotal = 287.97,
                shipping = 10.00
            )
        }
    }
}
