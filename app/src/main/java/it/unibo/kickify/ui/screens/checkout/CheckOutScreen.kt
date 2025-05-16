package it.unibo.kickify.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.CartAndCheckoutResume
import it.unibo.kickify.ui.composables.DialogWithImage
import it.unibo.kickify.ui.composables.InformationCard
import it.unibo.kickify.ui.composables.ScreenTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun CheckOutScreen(
    navController: NavController
){
    ScreenTemplate(
        screenTitle = stringResource(R.string.checkoutScreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false
    ) { contentPadding ->
        var showLoading: Boolean by rememberSaveable { mutableStateOf(false) }
        var showDialog by rememberSaveable { mutableStateOf(false) }

        LoadingAnimation(
            isLoading = showLoading,
            onLoadingComplete = {
                showLoading = false
                showDialog = true
            }
        ) {
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
                    shippingAddress = "Viale europa 638 47521 Cesena Italia",
                    payMethod = "paypal",
                    paymentDetails = "mario.rossi@gmail.com"
                )
                CartAndCheckoutResume(
                    subTotal = 287.97,
                    shipping = 10.00,
                    onButtonClickAction = {
                        showLoading = true

                    }
                )
            }
            if(showDialog){
                DialogWithImage(
                    imageVector = Icons.Outlined.Check,
                    mainMessage = stringResource(R.string.cartscreen_paymentSuccessful),
                    dismissButtonText = stringResource(R.string.cartscreen_backToShopping),
                    onDismissRequest = {
                        navController.navigate(KickifyRoute.Home) {
                            popUpTo(KickifyRoute.Home) { inclusive = true }
                        }
                    },

                )
            }
        }
    }
}

@Composable
fun LoadingAnimation(
    isLoading: Boolean,
    onLoadingComplete: () -> Unit,
    screenContent: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        screenContent()

        if (isLoading) {
            LaunchedEffect(Unit) {
                withContext(Dispatchers.Default){
                    delay(5000)
                }
                onLoadingComplete()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .pointerInput(Unit) {
                        detectTapGestures {} // block all click on screen
                    }
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }
    }
}