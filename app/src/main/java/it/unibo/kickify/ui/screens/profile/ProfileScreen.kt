package it.unibo.kickify.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import it.unibo.kickify.R
import it.unibo.kickify.data.models.Language
import it.unibo.kickify.data.models.PaymentMethodInfo
import it.unibo.kickify.data.models.PaymentMethods
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.PaymentMethodRow
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.settings.SettingsViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    profileViewModel: ProfileViewModel,
    achievementsViewModel: AchievementsViewModel
){
    val snackBarHostState = remember { SnackbarHostState() }

    val appLang by settingsViewModel.appLanguage.collectAsStateWithLifecycle()
    val userEmail by settingsViewModel.userId.collectAsStateWithLifecycle()
    val username by settingsViewModel.userName.collectAsStateWithLifecycle()
    val userImg by settingsViewModel.userImg.collectAsStateWithLifecycle()

    val user by profileViewModel.user.collectAsStateWithLifecycle()
    val addrList by profileViewModel.addressList.collectAsStateWithLifecycle()
    val paymentMethodList by profileViewModel.paymentMethods.collectAsStateWithLifecycle()
    val errorMessage by profileViewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by profileViewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(userEmail) {
        profileViewModel.getUserAddress(userEmail)
        profileViewModel.getProfile(userEmail)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackBarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
        }
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.profileScreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading,
        achievementsViewModel = achievementsViewModel
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            ProfileCardContainer(
                cardTitle = stringResource(R.string.userProfile),
                actionIcon = Icons.Outlined.Edit,
                action = {
                    navController.navigate(KickifyRoute.EditProfile(EditProfileSections.USER_INFO))
                }
            ) {
                UserProfileIcon(userImg,
                    userImgModifier = Modifier.size(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .align(Alignment.CenterHorizontally),
                    accountIconModifier = Modifier.size(120.dp).clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = if(user == null) username else "${user?.name} ${user?.surname}",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                        .padding(vertical = 4.dp).padding(start = 10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                TextInformationRow(if(user == null) userEmail else "${user?.email}")
                TextInformationRow("${stringResource(R.string.phone)}:", user?.phone ?: "-")
                TextInformationRow("${stringResource(R.string.language)}:",
                    Language.getLanguageStringFromCode(appLang))
            }

            ProfileActionRow(
                title = stringResource(R.string.myordersScreen_title),
                onButtonClickAction = { navController.navigate(KickifyRoute.MyOrders) },
                buttonText = stringResource(R.string.homescreen_seeAll),
            )
            ProfileActionRow(
                title = stringResource(R.string.wishlist_title),
                onButtonClickAction = { navController.navigate(KickifyRoute.Wishlist) },
                buttonText = stringResource(R.string.view)
            )

            ProfileCardContainer(
                cardTitle = stringResource(R.string.address),
                actionIcon = Icons.Outlined.Add,
                action = { navController.navigate(KickifyRoute.EditProfile(EditProfileSections.ADDRESS)) }
            ) {
                addrList.forEachIndexed { index, addr ->
                    AddressContainer(
                        index = index,
                        address = addr.street,
                        number = addr.civic,
                        city = addr.city,
                        province = addr.province,
                        postCode = addr.cap,
                        country = addr.nation,
                        defaultAddress = addr.default,
                        deleteAction = {
                            profileViewModel.deleteUserAddress(
                                userEmail, addr.street, addr.civic, addr.cap, addr.city
                            )
                        }
                    )
                }
            }

            ProfileCardContainer(
                cardTitle = stringResource(R.string.paymentMethod),
                actionIcon = Icons.Outlined.Add,
                action = {
                    navController.navigate(KickifyRoute.EditProfile(EditProfileSections.PAYMENT_METHOD))
                }
            ) {
                for(method in paymentMethodList){
                    if(method is PaymentMethodInfo.CreditCard){
                        PaymentMethodRow(
                            paymentMethod = method.brand,
                            endingCardNumber = method.last4,
                            cardExpires = "${method.expirationMonth}/${method.expirationYear}",
                            emailAddress = "",
                            deleteAction = { profileViewModel.deletePaymentMethod(method) }
                        )
                    } else if(method is PaymentMethodInfo.PayPal){
                        PaymentMethodRow(
                            paymentMethod = PaymentMethods.PAYPAL.toString(),
                            endingCardNumber = "",
                            cardExpires = "",
                            emailAddress = method.email,
                            deleteAction = { profileViewModel.deletePaymentMethod(method) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileIcon(
    userImg: String,
    userImgModifier: Modifier,
    accountIconModifier: Modifier
){
    if (userImg != "") {
        AsyncImage(
            model = userImg,
            contentDescription = "",
            placeholder = rememberVectorPainter(Icons.Outlined.AccountCircle),
            modifier = userImgModifier
        )
    } else { // otherwise use default user profile icon
        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = "",
            modifier = accountIconModifier
        )
    }
}

@Composable
fun ProfileActionRow(
    title: String,
    onButtonClickAction: () -> Unit,
    buttonText: String
){
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(10.dp)
            )
            Button(
                onClick = { onButtonClickAction() },
                modifier = Modifier.padding(end = 8.dp)
            ){
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun TextInformationRow(
    leftValue: String,
    rightValue: String? = null
){
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        if(rightValue == null || rightValue == ""){ // show only leftValue
            Column(modifier = Modifier.fillMaxWidth().padding(start = 10.dp)) {
                Text(text = leftValue, textAlign = TextAlign.Start)
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth(fraction = 0.45f).padding(start = 10.dp)) {
                Text(text = leftValue, textAlign = TextAlign.Start)
            }
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = rightValue, textAlign = TextAlign.Start)
            }
        }
    }
}

@Composable
fun ProfileCardContainer(
    cardTitle: String,
    actionIcon: ImageVector?,
    action: () -> Unit,
    columnCardContent: @Composable () -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = cardTitle,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(fraction = 0.7f).padding(10.dp)
                )
                if(actionIcon != null) {
                    IconButton(onClick = action) {
                        Icon(actionIcon, contentDescription = "Edit $cardTitle")
                    }
                }
            }
        }
        columnCardContent()
        Spacer(Modifier.height(8.dp))
    }
}