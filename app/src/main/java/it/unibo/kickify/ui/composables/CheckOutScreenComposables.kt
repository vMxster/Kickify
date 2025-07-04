package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.client.HttpClient
import it.unibo.kickify.R
import it.unibo.kickify.data.database.Address
import it.unibo.kickify.data.models.PaymentMethodInfo
import it.unibo.kickify.data.models.PaymentMethods
import it.unibo.kickify.data.models.PaymentMethods.AMEX
import it.unibo.kickify.data.models.PaymentMethods.MAESTRO
import it.unibo.kickify.data.models.PaymentMethods.MASTERCARD
import it.unibo.kickify.data.models.PaymentMethods.PAYPAL
import it.unibo.kickify.data.models.PaymentMethods.UNKNOWN
import it.unibo.kickify.data.models.PaymentMethods.VISA
import it.unibo.kickify.data.remote.OSMDataSource
import it.unibo.kickify.utils.Coordinates
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun CheckOutInformationRow(
    leadingIcon: ImageVector?,
    primaryText: String,
    secondaryText: String,
    showEditButton: Boolean = true,
    onEditInformation: () -> Unit = {}
){
    Row (
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        if(leadingIcon != null){
            Icon(leadingIcon,
                contentDescription = "",
                modifier = Modifier.padding(horizontal = 8.dp).size(30.dp)
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth(fraction = 0.9f)
                .padding(horizontal = 4.dp)
        ){
            Text(text = primaryText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = secondaryText)
        }
        if(showEditButton){
            IconButton(onClick = onEditInformation) {
                Icon(Icons.Outlined.Edit, contentDescription = "Edit $secondaryText",
                    modifier = Modifier.size(30.dp))
            }
        }
    }
}

@Composable
fun InformationSectionTitle(sectionTitle: String){
    Row (
        modifier = Modifier.padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AddressOnMapBox(
    searchAddressOrCoordinates: String,
    zoomLevel: Double,
    showAddressLabelIfAvailable: Boolean
){
    val ctx = LocalContext.current
    val osmDataSource = OSMDataSource(koinInject<HttpClient>())
    val coroutineScope = rememberCoroutineScope()
    val defaultCoordinates = Coordinates(41.9028, 12.4964)
    var foundOsmPlaceString: String by remember { mutableStateOf("") }

    var newCoord by remember { mutableStateOf(defaultCoordinates) }

    fun parseCoordinateString(input: String): Coordinates? {
        if (!input.contains(',')) {
            return null
        }
        val parts = input.split(',')
        if (parts.size != 2) {
            return null
        }
        val latitude = parts[0].trim().toDoubleOrNull()
        val longitude = parts[1].trim().toDoubleOrNull()

        return if (latitude != null && longitude != null) {
            Coordinates(latitude, longitude)
        } else {
            null
        }
    }

    LaunchedEffect(searchAddressOrCoordinates) {
        coroutineScope.launch {
            val searchCoords = parseCoordinateString(searchAddressOrCoordinates)

            if(searchCoords == null) { // if given invalid coordinates -> search as address
                val tmp = osmDataSource.searchPlaces(searchAddressOrCoordinates).firstOrNull()
                if (tmp != null) {
                    foundOsmPlaceString = tmp.displayName
                    newCoord = Coordinates(tmp.latitude, tmp.longitude)

                } else {
                    foundOsmPlaceString = ctx.getString(R.string.unavailableAddress)
                }
            } else {
                val tmp = osmDataSource.getPlace(searchCoords)
                foundOsmPlaceString = tmp.displayName
                newCoord = searchCoords
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val mapBoxModifier = Modifier.fillMaxWidth().requiredHeight(180.dp)
        if(newCoord != defaultCoordinates) {
            OSMMapBox(newCoord, zoomLevel, mapBoxModifier)
            if(showAddressLabelIfAvailable) {
                Text(foundOsmPlaceString)
            }
        } else {
            Icon(
                Icons.Outlined.Map, contentDescription = "Map unavailable",
                modifier = mapBoxModifier
            )
        }
    }
}

@Composable
fun paymentMethodIcon(paymentMethod: PaymentMethodInfo?) : ImageVector {
    if (paymentMethod != null) {
        when (paymentMethod) {
            is PaymentMethodInfo.CreditCard -> {
                return when (PaymentMethods.getFromString(paymentMethod.brand)) {
                    AMEX -> ImageVector.vectorResource(R.drawable.amex)
                    MAESTRO -> ImageVector.vectorResource(R.drawable.maestro)
                    MASTERCARD -> ImageVector.vectorResource(R.drawable.mastercard)
                    VISA -> ImageVector.vectorResource(R.drawable.visa)
                    else -> Icons.Outlined.Close
                }
            }
            is PaymentMethodInfo.PayPal -> ImageVector.vectorResource(R.drawable.paypal)
        }
    }
    return Icons.Outlined.Close
}

@Composable
fun paymentMethodIcon(payMethod: PaymentMethods): ImageVector {
    return when(payMethod){
        AMEX -> ImageVector.vectorResource(R.drawable.amex)
        MAESTRO -> ImageVector.vectorResource(R.drawable.maestro)
        MASTERCARD -> ImageVector.vectorResource(R.drawable.mastercard)
        PAYPAL -> ImageVector.vectorResource(R.drawable.paypal)
        VISA -> ImageVector.vectorResource(R.drawable.visa)
        UNKNOWN -> Icons.Outlined.Close
    }
}

@Composable
fun PaymentMethodRow(
    paymentMethod: String,
    endingCardNumber: String,
    cardExpires: String,
    emailAddress: String,
    deleteAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            paymentMethodIcon(PaymentMethods.getFromString(paymentMethod) ?: UNKNOWN),
            contentDescription = "$paymentMethod icon",
            modifier = Modifier.padding(horizontal = 6.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(fraction = 0.9f).padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (paymentMethod == PAYPAL.visibleName){
                Text(PAYPAL.visibleName)
                Text(emailAddress)
            } else {
                Text("$paymentMethod **** $endingCardNumber")
                Text("${stringResource(R.string.cardExpires)} $cardExpires")
            }
        }
        IconButton(
            onClick = { deleteAction() },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(Icons.Outlined.Delete, contentDescription = "")
        }
    }
}

fun getAddressText(address: Address): String {
    return "${address.street} ${address.civic}, ${address.cap}, ${address.city}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSelectorDialog(
    items: List<Address>,
    onDismissRequest: () -> Unit,
    onConfirm: (Address) -> Unit,
    onCancel: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState("")
    var selectedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedIndex) {
        if(items.isNotEmpty()) {
            textFieldState.setTextAndPlaceCursorAtEnd(getAddressText(items[selectedIndex]))
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.chooseShippingAddress),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        state = textFieldState,
                        readOnly = true,
                        lineLimits = TextFieldLineLimits.MultiLine(1, 5),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        items.forEachIndexed { index, option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        getAddressText(option),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    selectedIndex = index
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                            HorizontalDivider(
                                thickness = 3.dp,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onCancel() }) {
                    Text(stringResource(R.string.cancel))
                }

                TextButton(onClick = { onConfirm(items[selectedIndex]) }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        }
    }
}

fun getPaymentText(paymentMethodInfo: PaymentMethodInfo): String {
    if (paymentMethodInfo is PaymentMethodInfo.CreditCard) {
        return "${paymentMethodInfo.brand} **** ${paymentMethodInfo.last4}"

    } else if (paymentMethodInfo is PaymentMethodInfo.PayPal) { // Paypal
        return "${PAYPAL.visibleName} - ${paymentMethodInfo.email}"
    }
    return "-"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodSelectorDialog(
    items: List<PaymentMethodInfo>,
    onDismissRequest: () -> Unit,
    onConfirm: (PaymentMethodInfo) -> Unit,
    onCancel: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState("")
    var selectedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedIndex) {
        if(items.isNotEmpty()) {
            textFieldState.setTextAndPlaceCursorAtEnd(getPaymentText(items[selectedIndex]))
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.choosePaymentMethod),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        state = textFieldState,
                        readOnly = true,
                        lineLimits = TextFieldLineLimits.MultiLine(1, 3),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        items.forEachIndexed { index, option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        getPaymentText(option),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    selectedIndex = index
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                            HorizontalDivider(
                                thickness = 3.dp,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onCancel() }) {
                    Text(stringResource(R.string.cancel))
                }

                TextButton(onClick = { onConfirm(items[selectedIndex]) }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        }
    }
}