package it.unibo.kickify.ui.screens.productDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.ProductDetailsFooter
import it.unibo.kickify.ui.composables.ProductImage
import it.unibo.kickify.ui.composables.ProductLongDescription
import it.unibo.kickify.ui.composables.ProductName
import it.unibo.kickify.ui.composables.ProductPhotoGallery
import it.unibo.kickify.ui.composables.ProductPrice
import it.unibo.kickify.ui.composables.RatingBar
import it.unibo.kickify.ui.composables.SectionTitle
import it.unibo.kickify.ui.composables.SizesList

@Preview
@Composable
fun ProductDetailsScreen(
    navController: NavController = NavController(LocalContext.current),
    displayTitle: String = "Men's Shoes"
) {
    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = displayTitle
            )
        }
    ) { contentPadding ->
        val state = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(state),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val productInfo = mapOf("name" to "Nike Air Zoom",
                "descr" to "Nike Air Zoom is a responsive cushioning technology in footwear," +
                        "enhancing athletic performance and comfort across sneakers, while blending sport " +
                        "functionality with casual style.")
            val price = 97.99

            ProductImage(
                productInfo.getValue("name").toString(),
                modifier = Modifier.fillMaxWidth()
            )

            ProductName(
                productInfo.getValue("name").toString(),
                Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                ProductPrice(price)
                RatingBar(nrRatings = 4, listOf(5.0,5.0,5.0,5.0))
            }

            ProductLongDescription(longDescr = productInfo.getValue("descr"))

            SectionTitle(title = stringResource(R.string.prodDetails_gallery))
            ProductPhotoGallery(
                images = listOf(R.drawable.nike_air_zoom_1,
                    R.drawable.nike_air_zoom_2, R.drawable.nike_air_zoom_3),
                productName = productInfo.getValue("name").toString()
            )

            SectionTitle(title = stringResource(R.string.size))
            SizesList(
                sizeSelected = 40,
                sizesAvailability =  mapOf(
                    38 to true, 39 to true, 40 to true, 41 to true,
                    42 to false, 43 to true, 44 to true, 45 to true
                ),
                onSizeSelected = { }
            )
            ProductDetailsFooter(price)
        }
    }
}