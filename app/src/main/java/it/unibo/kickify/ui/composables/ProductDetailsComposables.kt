package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StarHalf
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unibo.kickify.R
import it.unibo.kickify.ui.theme.BluePrimary
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun ProductName(
    title: String,
    modifier: Modifier){
    Text(
        text = title,
        textAlign = TextAlign.Start,
        modifier = modifier,
        fontSize = 30.sp
    )
}

@Composable
fun ProductPrice(price: Double){
    Text(
        text = "€%.2f".format(price),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 6.dp),
        fontSize = 25.sp
    )
}

@Composable
fun ProductLongDescription(
    longDescr: String
){
    Text(
        text = longDescr,
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 17.sp
    )
}

@Composable
fun SectionTitle(
    title: String
){
    Spacer(Modifier.height(10.dp))
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp),
        style = MaterialTheme.typography.bodyLarge,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun RatingBar(
    nrRatings: Int,
    votesList: List<Double>
){
    val avgValue = votesList.sum() / nrRatings
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = "($nrRatings)",
            style = MaterialTheme.typography.bodyLarge
        )
        RatingStars(avgRatingToDisplay = avgValue)
        Text(
            text = "$avgValue",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ProductPhotoGallery(
    images: List<Int>,
    productName: String,
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        for (i in images.indices){
            Image(
                painterResource(images[i]),
                "$productName image $i",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.size(70.dp)
            )
            Spacer(Modifier.width(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SizesList(
    sizeSelected: Int?,
    sizesAvailability: Map<Int, Boolean>,
    onSizeSelected: (Int) -> Unit
){
    val scrollState = rememberScrollState()
    var selectedSize by remember { mutableStateOf(sizeSelected) }

    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth()
        .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        for (size in sizesAvailability.entries){
            Button(
                onClick = {
                    selectedSize = size.key
                    onSizeSelected(size.key)
                },
                enabled = size.value, // button is enabled if corresponding size is
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedSize == size.key) BluePrimary else MaterialTheme.colorScheme.background,
                    contentColor = if(selectedSize == size.key) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = ButtonDefaults.ExtraSmallContentPadding,
                shape = CircleShape,
                modifier = Modifier.size(54.dp),
            ){
                Text(text = size.key.toString(), fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
        }
    }
}

@Composable
fun ProductDetailsFooter(
    price: Double
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier.fillMaxWidth(fraction = 0.5f)
        ) {
            Text(stringResource((R.string.price)))
            ProductPrice(price = price)
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = BluePrimary
            ),
            onClick = {}
        ) {
            Text(
                text = stringResource(R.string.prodDetails_addToCart)
            )
        }
    }
}

@Composable
private fun RatingStars(
    modifier: Modifier = Modifier,
    avgRatingToDisplay: Double,
    stars: Int = 5,
    starsColor: Color = Color(red = 1.0f, green = 1.0f, blue = 0.0f)
) {
    val filledStars = floor(avgRatingToDisplay).toInt()
    val unfilledStars = (stars - ceil(avgRatingToDisplay)).toInt()
    val halfStar = !(avgRatingToDisplay.rem(1).equals(0.0))
    Row(modifier = modifier) {
        repeat(filledStars) {
            Icon(imageVector = Icons.Outlined.Star, contentDescription = null, tint = starsColor)
        }
        if (halfStar) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.StarHalf,
                contentDescription = null,
                tint = starsColor
            )
        }
        repeat(unfilledStars) {
            Icon(
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = starsColor
            )
        }
    }
}