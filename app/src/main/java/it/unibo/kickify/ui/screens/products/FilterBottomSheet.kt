package it.unibo.kickify.ui.screens.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import it.unibo.kickify.ui.composables.ShoesColorIndicator
import it.unibo.kickify.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    onApplyFilter: (FilterState) -> Unit,
    onResetFilter: () -> Unit,
    initialFilterState: FilterState
){
    // Gestione dello stato interno dei filtri
    var priceRange by remember(initialFilterState) { mutableStateOf(initialFilterState.priceRange) }
    var selectedBrands by remember(initialFilterState) { mutableStateOf(initialFilterState.selectedBrands) }
    var selectedColors by remember(initialFilterState) { mutableStateOf(initialFilterState.selectedColors) }
    var selectedSizes by remember(initialFilterState) { mutableStateOf(initialFilterState.selectedSizes) }
    var selectedGender by remember(initialFilterState) { mutableStateOf(initialFilterState.selectedGender) }
    var orderBy by remember(initialFilterState) { mutableStateOf(initialFilterState.orderBy) }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
        contentWindowInsets = { WindowInsets.safeDrawing }
    ){
        val sizeList = listOf(37, 38, 39, 40, 41, 42, 43, 44, 45, 46)
        val brandsList = listOf("Adidas", "Nike", "Puma")
        val colorList = listOf(
            Color.Black,
            Color.Blue,
            Color.Green,
            Color.Yellow,
            Color.Red,
            Color.White
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ){
            FilterTitleRow(onResetFilter = {
                // Reset dei filtri locali
                priceRange = 40f..500f
                selectedBrands = emptySet()
                selectedColors = emptySet()
                selectedSizes = emptySet()
                selectedGender = null
                orderBy = OrderBy.NONE
                onResetFilter()
            })
            Spacer(Modifier.height(8.dp))

            FilterGroupTitle(stringResource(R.string.filterBottomSheet_orderBy))
            val orderOptions = listOf(
                OrderBy.NONE to stringResource(R.string.filterBottomSheet_none),
                OrderBy.PRICE_LOW_HIGH to stringResource(R.string.filterBottomSheet_priceLowHigh),
                OrderBy.PRICE_HIGH_LOW to stringResource(R.string.filterBottomSheet_priceHighLow),
                OrderBy.ALPHABETICAL to stringResource(R.string.filterBottomSheet_alphabetical)
            )

            val orderTexts = orderOptions.map { it.second }
            FilterButtonGroup(
                options = orderTexts,
                initialSelectedIndex = orderOptions.indexOfFirst { it.first == orderBy },
                onSelectedChanged = { index ->
                    orderBy = orderOptions[index].first
                }
            )
            Spacer(Modifier.height(8.dp))

            FilterGroupTitle(stringResource(R.string.filterBottomSheet_gender))
            GenderGroup(
                selectedGender = selectedGender,
                onGenderSelected = { gender ->
                    selectedGender = if (selectedGender == gender) null else gender
                }
            )
            Spacer(Modifier.height(8.dp))

            FilterGroupTitle(stringResource(R.string.brand))
            BrandsGroup(
                brandsList = brandsList,
                selectedBrands = selectedBrands,
                onBrandSelected = { brand ->
                    selectedBrands = if (selectedBrands.contains(brand)) {
                        selectedBrands - brand
                    } else {
                        selectedBrands + brand
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            FilterGroupTitle(stringResource(R.string.color))
            ColorGroup(
                colorList = colorList,
                selectedColors = selectedColors,
                onColorSelected = { colorStr ->
                    selectedColors = if (selectedColors.contains(colorStr)) {
                        selectedColors - colorStr
                    } else {
                        selectedColors + colorStr
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            FilterGroupTitle(stringResource(R.string.size))
            SizeGroup(
                sizeList = sizeList,
                selectedSizes = selectedSizes,
                onSizeSelected = { size ->
                    selectedSizes = if (selectedSizes.contains(size)) {
                        selectedSizes - size
                    } else {
                        selectedSizes + size
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            FilterGroupTitle(stringResource(R.string.price))
            PriceSlider(
                minValue = 40,
                maxValue = 500,
                initialPriceRange = priceRange,
                onRangeChange = { range -> priceRange = range }
            )
            Spacer(Modifier.height(8.dp))

            ApplyFilterButton(onClick = {
                val newFilterState = FilterState(
                    priceRange = priceRange,
                    selectedBrands = selectedBrands,
                    selectedColors = selectedColors,
                    selectedSizes = selectedSizes,
                    selectedGender = selectedGender,
                    orderBy = orderBy
                )
                onApplyFilter(newFilterState)
            })
        }
    }
}

@Composable
fun ApplyFilterButton(onClick: () -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
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
    maxValue: Int,
    initialPriceRange: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    var currentRange by remember { mutableStateOf(initialPriceRange) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        val rangeStart = "%.0f".format(currentRange.start)
        val rangeEnd = "%.0f".format(currentRange.endInclusive)

        RangeSlider(
            value = currentRange,
            onValueChange = { range ->
                currentRange = range
                onRangeChange(range)
            },
            valueRange = minValue.toFloat()..maxValue.toFloat(),
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
            Text(text = stringResource(R.string.filterBottomSheet_reset))
        }
    }
}

@Composable
fun FilterButtonGroup(
    options: List<String>,
    initialSelectedIndex: Int = 0,
    onSelectedChanged: (Int) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(initialSelectedIndex) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ){
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
                    .clickable {
                        selectedIndex = options.indexOf(option)
                        onSelectedChanged(selectedIndex)
                    }
            ) {
                RadioButton(
                    selected = (selectedIndex == options.indexOf(option)),
                    onClick = {
                        selectedIndex = options.indexOf(option)
                        onSelectedChanged(selectedIndex)
                    }
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun FilterGroupTitle(title: String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 11.dp, horizontal = 8.dp)
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun BrandsGroup(
    brandsList: List<String>,
    selectedBrands: Set<String>,
    onBrandSelected: (String) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        brandsList.forEach { brand ->
            SingleOptionSelectableButton(
                shownText = brand,
                isSelected = selectedBrands.contains(brand),
                onClick = { onBrandSelected(brand) }
            )
            Spacer(Modifier.width(10.dp))
        }
    }
}

@Composable
fun GenderGroup(
    selectedGender: ShopCategory?,
    onGenderSelected: (ShopCategory) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        ShopCategory.entries.forEach { category ->
            SingleOptionSelectableButton(
                shownText = category.toString(),
                isSelected = selectedGender == category,
                onClick = { onGenderSelected(category) }
            )
            Spacer(Modifier.width(10.dp))
        }
    }
}

@Composable
fun ColorGroup(
    colorList: List<Color>,
    selectedColors: Set<String>,
    onColorSelected: (String) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        for (color in colorList) {
            val colorString = color.toString()
            SingleColorSelectableButton(
                color = color,
                isSelected = selectedColors.contains(colorString),
                onClick = { onColorSelected(colorString) }
            )
            Spacer(Modifier.width(14.dp))
        }
    }
}

@Composable
fun SizeGroup(
    sizeList: List<Int>,
    selectedSizes: Set<Int>,
    onSizeSelected: (Int) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        for (size in sizeList){
            SingleSizeButton(
                shownText = size.toString(),
                isSelected = selectedSizes.contains(size),
                onClick = { onSizeSelected(size) }
            )
            Spacer(Modifier.width(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SingleSizeButton(
    shownText: String,
    isSelected: Boolean,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) BluePrimary else MaterialTheme.colorScheme.background,
            contentColor = if(isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = ButtonDefaults.ExtraSmallContentPadding,
        shape = CircleShape,
        modifier = Modifier.size(54.dp),
    ){
        Text(text = shownText, fontSize = 18.sp)
    }
}

@Composable
fun SingleOptionSelectableButton(
    shownText: String,
    isSelected: Boolean,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) BluePrimary else MaterialTheme.colorScheme.background,
            contentColor = if(isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    ){
        Text(text = shownText, fontSize = 18.sp)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SingleColorSelectableButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) BluePrimary else MaterialTheme.colorScheme.background,
        ),
        contentPadding = ButtonDefaults.ExtraSmallContentPadding
    ){
        ShoesColorIndicator(color, indicatorSize = 32.dp)
    }
}