package it.unibo.kickify.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.database.Address
import it.unibo.kickify.data.models.PaymentMethodInfo
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.AddressOnMapBox
import it.unibo.kickify.ui.composables.AddressSelectorDialog
import it.unibo.kickify.ui.composables.CartAndCheckoutResume
import it.unibo.kickify.ui.composables.CheckOutInformationRow
import it.unibo.kickify.ui.composables.DialogWithImage
import it.unibo.kickify.ui.composables.InformationSectionTitle
import it.unibo.kickify.ui.composables.PaymentMethodSelectorDialog
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.composables.getAddressText
import it.unibo.kickify.ui.composables.getPaymentText
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.cart.CartViewModel
import it.unibo.kickify.ui.screens.profile.ProfileViewModel
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun CheckOutScreen(
    navController: NavController,
    achievementsViewModel: AchievementsViewModel,
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    cartViewModel: CartViewModel
){
    val userEmail by settingsViewModel.userId.collectAsStateWithLifecycle()
    val user by profileViewModel.user.collectAsStateWithLifecycle()
    val addrList by profileViewModel.addressList.collectAsStateWithLifecycle()
    val payMethodList by profileViewModel.paymentMethods.collectAsStateWithLifecycle()
    val isLoadingProfile by profileViewModel.isLoading.collectAsStateWithLifecycle()
    val isLoadingCart by cartViewModel.isLoading.collectAsStateWithLifecycle()
    val subtotal by cartViewModel.subTotal.collectAsStateWithLifecycle()
    val shipping by cartViewModel.shippingCost.collectAsStateWithLifecycle()
    val totalCost by cartViewModel.total.collectAsStateWithLifecycle()

    LaunchedEffect(userEmail) {
        profileViewModel.getUserAddress(userEmail)
        profileViewModel.getProfile(userEmail)
        profileViewModel.getPaymentMethods(userEmail)
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.checkoutScreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false,
        achievementsViewModel = achievementsViewModel,
        showLoadingOverlay = isLoadingCart || isLoadingProfile
    ) {
        var showLoading: Boolean by rememberSaveable { mutableStateOf(false) }
        var showDialog by rememberSaveable { mutableStateOf(false) }

        var showAddressSelectorDialog by rememberSaveable { mutableStateOf(false) }
        var showPaymentMethodSelectorDialog by rememberSaveable { mutableStateOf(false) }

        var selectedAddress by remember { mutableStateOf<Address?>(null) }
        var selectedPaymentMethod by remember { mutableStateOf<PaymentMethodInfo?>(null) }

        var enabledCheckoutButton by remember { mutableStateOf(false) }

        LaunchedEffect(selectedPaymentMethod, selectedAddress) {
            enabledCheckoutButton = selectedAddress != null && selectedPaymentMethod != null
        }

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
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Spacer(Modifier.height(10.dp))

                        InformationSectionTitle(stringResource(R.string.checkoutScreen_contactInformation))
                        Spacer(Modifier.height(6.dp))
                        CheckOutInformationRow(
                            leadingIcon = Icons.Outlined.Email,
                            primaryText = user?.email ?: "-",
                            secondaryText = stringResource(R.string.checkoutScreen_email),
                            showEditButton = false
                        )
                        CheckOutInformationRow(
                            leadingIcon = Icons.Outlined.Phone,
                            primaryText = user?.phone ?: "-",
                            secondaryText = stringResource(R.string.phone),
                            showEditButton = false
                        )
                        Spacer(Modifier.height(10.dp))

                        InformationSectionTitle(
                            sectionTitle = if(selectedAddress == null) stringResource(R.string.chooseShippingAddress)
                            else stringResource(R.string.settings_shippingAddress)
                        )
                        Spacer(Modifier.height(6.dp))
                        CheckOutInformationRow(
                            leadingIcon = null,
                            primaryText = selectedAddress?.let { getAddressText(it) }
                                ?: stringResource(R.string.noShippingAddressSelected),
                            secondaryText = "",
                            showEditButton = true,
                            onEditInformation = { showAddressSelectorDialog = true }
                        )
                        AddressOnMapBox(
                            address = selectedAddress?.let { getAddressText(it) } ?: "",
                            zoomLevel = 19.0,
                            showAddressLabelIfAvailable = false
                        )
                        Spacer(Modifier.height(10.dp))

                        InformationSectionTitle(stringResource(R.string.paymentMethods))
                        Spacer(Modifier.height(6.dp))

                        CheckOutInformationRow(
                            leadingIcon = null,
                            primaryText = selectedPaymentMethod?.let { getPaymentText(it) }
                                ?: stringResource(R.string.noPaymentMethodSelected),
                            secondaryText = "",
                            showEditButton = true,
                            onEditInformation = { showPaymentMethodSelectorDialog = true }
                        )
                    }
                }

                CartAndCheckoutResume(
                    subTotal = subtotal,
                    shipping = shipping,
                    total = totalCost,
                    checkoutButtonEnabled = enabledCheckoutButton,
                    onButtonClickAction = {
                        selectedAddress?.let {
                            selectedPaymentMethod?.let { it1 ->
                                cartViewModel.placeOrder(
                                    address = it,
                                    paymentMethod = it1
                                )
                            }
                        }
                    }
                )
            }

            if(showAddressSelectorDialog){
                AddressSelectorDialog(
                    items = addrList,
                    onDismissRequest = { showAddressSelectorDialog = false },
                    onConfirm = { selectedAddr ->
                        showAddressSelectorDialog = false
                        selectedAddress = selectedAddr
                    },
                    onCancel = { showAddressSelectorDialog = false}
                )
            }

            if(showPaymentMethodSelectorDialog){
                PaymentMethodSelectorDialog(
                    items = payMethodList,
                    onDismissRequest = { showPaymentMethodSelectorDialog = false },
                    onConfirm = { selectedPay ->
                        showPaymentMethodSelectorDialog = false
                        selectedPaymentMethod = selectedPay
                    },
                    onCancel = { showPaymentMethodSelectorDialog = false }
                )
            }
            if(showDialog){
                DialogWithImage(
                    imageVector = Icons.Outlined.Check,
                    mainMessage = stringResource(R.string.cartscreen_paymentSuccessful),
                    dismissButtonText = stringResource(R.string.cartscreen_backToShopping),
                    onDismissRequest = {
                        achievementsViewModel.achieveAchievement(3)
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