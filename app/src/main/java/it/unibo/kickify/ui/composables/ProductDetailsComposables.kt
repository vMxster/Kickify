package it.unibo.kickify.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StarHalf
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import it.unibo.kickify.R
import it.unibo.kickify.data.database.Image
import it.unibo.kickify.data.database.ReviewWithUserInfo
import it.unibo.kickify.ui.theme.BluePrimary
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun ProductName(
    brand: String,
    name: String,
    modifier: Modifier){
    Text(
        text = "$brand $name",
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
    title: String,
    buttonIcon: ImageVector? = null,
    iconDescription: String? = null,
    onButtonClick: () -> Unit = {}
){
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if(buttonIcon != null){
            IconButton(onClick = onButtonClick) {
                Icon(buttonIcon, contentDescription = iconDescription)
            }
        }
    }
}

@Composable
fun RatingBar(nrRatings: Int, votesList: List<Double>){
    val avgValue = if(votesList.isNotEmpty()) votesList.sum()/nrRatings else 0.0
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "($nrRatings)",
            style = MaterialTheme.typography.bodyLarge
        )
        RatingStars(ratingToDisplay = avgValue)
        Text(
            text = "$avgValue",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ProductPhotoGallery(
    images: List<Image>, productName: String,
    clickOnImageAction: (Int) -> Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        for (i in images){
            AsyncImage(
                model = i.url,
                contentDescription = "$productName image ${i.number}",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.size(95.dp)
                    .clickable { clickOnImageAction(i.number) }
            )
            Spacer(Modifier.width(18.dp))
        }
    }
}

private fun parseColorName(colorName: String): Color {
    return when (colorName.lowercase()) {
        "red" -> Color.Red
        "green" -> Color.Green
        "blue" -> Color.Blue
        "white" -> Color.White
        "black" -> Color.Black
        "gray", "grey" -> Color.Gray
        "cyan" -> Color.Cyan
        "magenta" -> Color.Magenta
        "yellow" -> Color.Yellow
        "darkgray", "darkgrey" -> Color.DarkGray
        "lightgray", "lightgrey" -> Color.LightGray
        else -> Color.Transparent
    }
}

@Composable
fun ColorsList(
    colorSelected: Color?,
    colorAvailability: Map<String, Boolean>,
    onColorSelected: (Color) -> Unit
){
    var selectedColor by remember { mutableStateOf(colorSelected) }

    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        for ((colorString, available) in colorAvailability.entries){
            val color = parseColorName(colorString)

            if(available) { // if color is available
                Button(
                    onClick = {
                        selectedColor = color
                        onColorSelected(color)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color
                    ),
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                        .border(
                            width = 4.dp,
                            color = if (selectedColor == color) BluePrimary else Color.Transparent,
                            shape = CircleShape
                        )
                ) { }

                Spacer(Modifier.width(18.dp))
            }
        }
    }

    Spacer(Modifier.height(10.dp))
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
    price: Double,
    addProductToCartAction: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp),
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
            onClick = { addProductToCartAction() }
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
    ratingToDisplay: Double,
    stars: Int = 5,
    starsColor: Color = Color(red = 255, green = 120, blue = 0)
) {
    val filledStars = floor(ratingToDisplay).toInt()
    val unfilledStars = (stars - ceil(ratingToDisplay)).toInt()
    val halfStar = !(ratingToDisplay.rem(1).equals(0.0))
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

@Composable
fun ReviewCard(
    reviewWithUserInfo: ReviewWithUserInfo,
    showDeleteButton: Boolean,
    deleteReviewAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${reviewWithUserInfo.name} ${reviewWithUserInfo.surname}",
                    style = MaterialTheme.typography.bodyLarge
                )
                if(showDeleteButton) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = deleteReviewAction) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.prodDetails_deleteReview)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RatingStars(ratingToDisplay = reviewWithUserInfo.review.vote)
                Text(text = reviewWithUserInfo.review.reviewDate)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reviewWithUserInfo.review.comment,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}