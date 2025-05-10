package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unibo.kickify.R
import it.unibo.kickify.data.models.PaymentMethod
import it.unibo.kickify.data.models.PaymentMethod.*

@Composable
fun InformationCard(
    emailAddress: String,
    phoneNr: String,
    shippingAddress: String,
    payMethod: String,
    paymentDetails: String
){
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(vertical = 6.dp)
            .fillMaxWidth()
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
                primaryText = emailAddress,
                secondaryText = stringResource(R.string.checkoutScreen_email),
                showEditButton = true,
                onEditInformation = {}
            )
            CheckOutInformationRow(
                leadingIcon = Icons.Outlined.Phone,
                primaryText = phoneNr,
                secondaryText = stringResource(R.string.checkoutScreen_phone),
                showEditButton = true,
                onEditInformation = {}
            )
            CheckOutInformationRow(
                leadingIcon = null,
                primaryText = shippingAddress,
                secondaryText = stringResource(R.string.checkoutScreen_address),
                showEditButton = true,
                onEditInformation = {}
            )

            Spacer(Modifier.height(10.dp))
            AddressOnMapBox(address = shippingAddress)

            Spacer(Modifier.height(10.dp))
            InformationSectionTitle(stringResource(R.string.paymentMethod))

            Spacer(Modifier.height(6.dp))
            CheckOutInformationRow(
                leadingIcon = paymentMethodIcon(payMethod),
                primaryText = payMethod,
                secondaryText = paymentDetails,
                showEditButton = true,
                onEditInformation = { }
            )
        }
    }
}

@Composable
fun CheckOutInformationRow(
    leadingIcon: ImageVector?,
    primaryText: String,
    secondaryText: String,
    showEditButton: Boolean = true,
    /** action to execute when the edit button is clicked, by default does nothing */
    onEditInformation: () -> Unit = {}
){
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
        .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        if(leadingIcon != null){
            Image(leadingIcon,
                contentDescription = "$secondaryText icon",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth(fraction = 0.8f)
                .padding(start = 8.dp)
        ){
            Text(text = primaryText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = secondaryText)
        }
        if(showEditButton){
            Icon(Icons.Outlined.Edit, contentDescription = "Edit $secondaryText",
                Modifier.clickable { onEditInformation() }
            )
        }
    }
}

@Composable
fun InformationSectionTitle(
    sectionTitle: String
){
    Row (modifier = Modifier
        .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start){
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AddressOnMapBox(
    address: String
){
    Icon(Icons.Outlined.Map, contentDescription = "",
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp))
}

@Composable
fun paymentMethodIcon(paymentMethod: String) : ImageVector {
    val method = PaymentMethod.getFromString(paymentMethod)
    return if (method != null) {
        when (method) {
            AMEX -> ImageVector.vectorResource(R.drawable.amex)
            MAESTRO -> ImageVector.vectorResource(R.drawable.maestro)
            MASTERCARD -> ImageVector.vectorResource(R.drawable.mastercard)
            PAYPAL -> ImageVector.vectorResource(R.drawable.paypal)
            VISA -> ImageVector.vectorResource(R.drawable.visa)
        }
    } else {
        Icons.Outlined.Close
    }
}

@Composable
fun PaymentMethodRow(
    paymentMethod: String,
    endingCardNumber: String,
    cardExpires: String,
    emailAddress: String
){
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Image(
            paymentMethodIcon(paymentMethod),
            contentDescription = "$paymentMethod icon",
            modifier = Modifier.padding(start = 9.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(fraction = 0.9f).padding(end = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if(PaymentMethod.getFromString(paymentMethod) == PAYPAL){
                Text(paymentMethod)
                Text(emailAddress)
            } else {
                Text("$paymentMethod **** $endingCardNumber")
                Text("${stringResource(R.string.cardExpires)} $cardExpires")
            }
        }
    }
}