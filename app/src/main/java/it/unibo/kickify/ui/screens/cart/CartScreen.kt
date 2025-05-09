package it.unibo.kickify.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.CartAndCheckoutResume
import it.unibo.kickify.ui.composables.CartItemsList

@Composable
fun CartScreen(
    navController: NavController
){
    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.cartscreen_title)
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CartItemsList()
            Spacer(Modifier.height(20.dp))
            CartAndCheckoutResume(
                subTotal = (69.99+129.99+87.99),
                shipping = 10.0,
                onButtonClickAction = { navController.navigate(KickifyRoute.Checkout) })
        }
    }
}