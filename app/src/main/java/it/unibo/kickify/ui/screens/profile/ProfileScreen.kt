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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.camerax.CameraXutils
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.PaymentMethodRow
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.settings.SettingsViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    cameraXutils: CameraXutils,
    settingsViewModel: SettingsViewModel
){
    ScreenTemplate(
        screenTitle = stringResource(R.string.profileScreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
    ) { contentPadding ->
        val scrollState = rememberScrollState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(scrollState)
        ) {

            ProfileCardContainer(cardTitle = "User Profile") {
                //val userImg
                //if (userImg == null) {
                    /*Image(
                    bitmap = userBitmap.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                    )*/
                // } else { // otherwise use user profile icon
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                )
                //}

                Text("Mario Rossi", modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    .padding(vertical = 4.dp).padding(start = 10.dp),
                    style = MaterialTheme.typography.titleMedium)
                TextInformationRow("email@example.com")
                TextInformationRow("Phone:", "+39 321 1234567")
                TextInformationRow("Language:", "English")
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

            ProfileCardContainer(cardTitle = "Address") {
                val addressList = listOf(
                    listOf("Address 1", "Via Roma, 100", "Cesena", "Forlì-Cesena", "Emilia Romagna", "47521", "Italia"),
                    listOf("Address 2", "Via della vittoria, 421", "Forlì", "Forlì-Cesena", "Emilia Romagna", "47121", "Italia")
                )
                for(addr in addressList){
                    AddressContainer(
                        addressDescription = addr[0],
                        address = addr[1],
                        city = addr[2],
                        province = addr[3],
                        region = addr[4],
                        postCode = addr[5],
                        country = addr[6]
                    )
                }
            }

            val payMethods = listOf("Paypal", "Maestro")
            val cardInfo = listOf(listOf("", "", "email@example.com"), listOf("1234", "01/29", ""))
            ProfileCardContainer(cardTitle = stringResource(R.string.paymentMethod)) {
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
            Column(modifier = Modifier.fillMaxWidth(fraction = 0.4f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                )
            }
            Column {
                Button(
                    onClick = { onButtonClickAction() },
                    modifier = Modifier.padding(end = 8.dp)
                ){
                    Text(text = buttonText)
                }
            }
        }
    }
}

@Composable
fun AddressContainer(
    addressDescription: String,
    address: String,
    city: String,
    province: String,
    region: String,
    postCode: String,
    country: String
){
    val txtModifier = Modifier.fillMaxWidth().padding(start = 10.dp).padding(vertical = 2.dp)
    Column(modifier = txtModifier.padding(vertical = 8.dp)) {
        Text(text = addressDescription, modifier = txtModifier,
            style = MaterialTheme.typography.titleMedium
        )
        Text(text = "$address, $city, $postCode", textAlign = TextAlign.Start, modifier = txtModifier)
        Text(text = "$province, $region", textAlign = TextAlign.Start, modifier = txtModifier)
        Text(text = country, textAlign = TextAlign.Start, modifier = txtModifier)
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
            Column(modifier = Modifier.fillMaxWidth(fraction = 0.3f).padding(start = 10.dp)) {
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
            Text(
                text = cardTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            )
        }
        columnCardContent()
        Spacer(Modifier.height(8.dp))
    }
}