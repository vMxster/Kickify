package it.unibo.kickify.ui.screens.productList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberRangeSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unibo.kickify.R
import it.unibo.kickify.data.models.ShopCategory
import it.unibo.kickify.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    onResetFilter: () -> Unit
){
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState
    ){
        val sizeList = listOf(40, 41, 42, 43, 44, 45)

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp)
                .padding(vertical = 8.dp)
        ){
            FilterTitleRow(onResetFilter = onResetFilter)

            Spacer(Modifier.height(8.dp))
            FilterGroupTitle(stringResource(R.string.filterBottomSheet_gender))
            GenderGroup()

            Spacer(Modifier.height(8.dp))
            FilterGroupTitle(stringResource(R.string.size))
            SizeGroup(sizeList)

            Spacer(Modifier.height(8.dp))
            FilterGroupTitle(stringResource(R.string.price))
            PriceSlider(minValue = 40, maxValue = 500)

            Spacer(Modifier.height(8.dp))
            ApplyFilterButton(onClick = {})
        }
    }
}

@Composable
fun ApplyFilterButton(onClick: () -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(vertical = 8.dp)
    ){
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.filterBottomSheet_apply))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceSlider(
    minValue: Int,
    maxValue: Int
) {
    val rangeSliderState =
        rememberRangeSliderState(
            activeRangeStart = minValue.toFloat(),
            activeRangeEnd = maxValue.toFloat(),
            valueRange = minValue.toFloat()..maxValue.toFloat(),
            onValueChangeFinished = {
                // launch some business logic update with the state you hold
                // viewModel.updateSelectedSliderValue(sliderPosition)
            }
        )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        val rangeStart = "%.0f".format(rangeSliderState.activeRangeStart)
        val rangeEnd = "%.0f".format(rangeSliderState.activeRangeEnd)
        RangeSlider(
            state = rangeSliderState,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            Text("€$minValue")
            Text("€${rangeStart} - €${rangeEnd}")
            Text("€$maxValue")
        }
    }
}

@Composable
fun FilterTitleRow(onResetFilter: () -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ){
        Text(
            text = stringResource(R.string.filterBottomSheet_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
        TextButton(
            onClick = { onResetFilter() }
        ) {
            Text(
                text = stringResource(R.string.filterBottomSheet_reset)
            )
        }
    }
}

@Composable
fun FilterGroupTitle(title: String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun GenderGroup(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        ShopCategory.entries.forEach {
            SingleGenderButton(it.toString())
            Spacer(Modifier.width(10.dp))
        }
    }
}

@Composable
fun SizeGroup(
    sizeList: List<Int>
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (size in sizeList){
            SingleSizeButton(size.toString())
            Spacer(Modifier.width(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SingleSizeButton(shownText: String){
    var selected by remember { mutableStateOf(false) }

    Button(
        onClick = { selected = !selected },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) BluePrimary else MaterialTheme.colorScheme.background,
            contentColor = if(selected) Color.White else MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = ButtonDefaults.ExtraSmallContentPadding,
        shape = CircleShape,
        modifier = Modifier.size(54.dp),
    ){
        Text(text = shownText, fontSize = 18.sp)
    }
}

@Composable
fun SingleGenderButton(shownText: String){
    var selected by remember { mutableStateOf(false) }

    Button(
        onClick = { selected = !selected },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) BluePrimary else MaterialTheme.colorScheme.background,
            contentColor = if(selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    ){
        Text(text = shownText, fontSize = 18.sp)
    }
}