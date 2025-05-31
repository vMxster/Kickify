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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import it.unibo.kickify.R
import it.unibo.kickify.camerax.CameraXUtils
import it.unibo.kickify.data.database.User
import it.unibo.kickify.data.models.Language
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.PaymentMethodRow
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.settings.EditProfileSections
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    navController: NavController,
    cameraXUtils: CameraXUtils,
    settingsViewModel: SettingsViewModel
){
    val appLang by settingsViewModel.appLanguage.collectAsStateWithLifecycle()
    val userID by settingsViewModel.userId.collectAsStateWithLifecycle()
    val username by settingsViewModel.userName.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val appRepo = koinInject<AppRepository>()
    var user: User? = null

    LaunchedEffect(Unit) {
        delay(500)
        coroutineScope.launch {
            val res = appRepo.getUserProfile(userID)
            val resUser = res.getOrNull()
            if(res.isSuccess && resUser != null){
                user = resUser
            }
        }
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.profileScreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            ProfileCardContainer(
                cardTitle = stringResource(R.string.userProfile),
                showEditIcon = true,
                editAction = {
                    navController.navigate(KickifyRoute.EditProfile(EditProfileSections.USER_INFO))
                }
            ) {
                if (user?.urlPhoto != null) {
                    AsyncImage(
                        model = user?.urlPhoto,
                        contentDescription = "",
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else { // otherwise use default user profile icon
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "",
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                Text(username, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    .padding(vertical = 4.dp).padding(start = 10.dp),
                    style = MaterialTheme.typography.titleMedium)
                TextInformationRow(userID)
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
                cardTitle = stringResource(R.string.checkoutScreen_address),
                showEditIcon = false,
                editAction = { }
            ) {
                val addressList = listOf(
                    listOf("Address 1", "Via Roma", "100", "Cesena", "Forli-Cesena", "Emilia Romagna", "47521", "Italia"),
                    listOf("Address 2", "Via della vittoria", "421", "Forlì", "Forlì-Cesena", "Emilia Romagna", "47121", "Italia")
                )
                for(addr in addressList){
                    AddressContainer(
                        addressDescription = addr[0],
                        address = addr[1],
                        number = addr[2],
                        city = addr[3],
                        province = addr[4],
                        region = addr[5],
                        postCode = addr[6],
                        country = addr[7]
                    )
                }
            }

            val payMethods = listOf("Paypal", "Maestro")
            val cardInfo = listOf(listOf("", "", "email@example.com"), listOf("1234", "01/29", ""))
            ProfileCardContainer(
                cardTitle = stringResource(R.string.paymentMethod),
                showEditIcon = false,
                editAction = {
                    navController.navigate(KickifyRoute.EditProfile(EditProfileSections.PAYMENT_METHOD))
                }
            ) {
                for(i in payMethods.indices){
                    PaymentMethodRow(payMethods[i], cardInfo[i][0], cardInfo[i][1], cardInfo[i][2])
                }
            }
        }
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
fun AddressContainer(
    addressDescription: String,
    address: String,
    number: String,
    city: String,
    province: String,
    region: String,
    postCode: String,
    country: String
){
    val txtModifier = Modifier.fillMaxWidth().padding(start = 10.dp).padding(vertical = 2.dp)
    Row(
        modifier = txtModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(modifier = Modifier.fillMaxWidth(fraction = 0.7f).padding(vertical = 8.dp)) {
            Text(text = addressDescription, modifier = txtModifier,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "$address, $number", textAlign = TextAlign.Start, modifier = txtModifier)
            Text(text = "$city, $postCode, $province", textAlign = TextAlign.Start, modifier = txtModifier)
            Text(text = "$region, $country", textAlign = TextAlign.Start, modifier = txtModifier)
        }
        IconButton(
            onClick = {},
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = "Edit $addressDescription")
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
    showEditIcon: Boolean,
    editAction: () -> Unit,
    columnCardContent: @Composable () -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(vertical = 6.dp)
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
                if(showEditIcon) {
                    IconButton(
                        onClick = editAction
                    ) {
                        Icon(Icons.Outlined.Edit,
                            contentDescription = "Edit $cardTitle"
                        )
                    }
                }
            }
        }
        columnCardContent()
        Spacer(Modifier.height(8.dp))
    }
}