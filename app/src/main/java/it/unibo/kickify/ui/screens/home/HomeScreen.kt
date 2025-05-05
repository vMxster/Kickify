package it.unibo.kickify.ui.screens.home

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.models.ShopCategory
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.HomeScreenCategory
import it.unibo.kickify.ui.composables.HomeScreenSectionSquareProductCards
import it.unibo.kickify.ui.composables.HomeScreenSmallBrandLogos
import it.unibo.kickify.ui.composables.SearchRoundedTextField

@Composable
fun HomeScreen(
    navController: NavController,
){
    val brands = listOf(R.drawable.nike,
        R.drawable.puma, R.drawable.under_armour,
        R.drawable.adidas, R.drawable.converse)

    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.app_name)
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { contentPadding ->
        val brandIconsScrollState = rememberScrollState()
        val state = rememberScrollState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(state)
        ){
            SearchRoundedTextField(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                onSearchAction = {
                    navController.navigate(KickifyRoute.ProductList)
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp).padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                ShopCategory.entries.forEach {
                    HomeScreenCategory(
                        it,
                        onClick = { category ->
                            navController.navigate(
                                KickifyRoute.ProductListWithCategory(category)
                            )
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(brandIconsScrollState)
                    .padding(vertical = 6.dp).padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for(brandResID in brands){
                    val brandName = stringResource(brandResID)
                    HomeScreenSmallBrandLogos(
                        brandResID,
                        onClick = {
                            navController.navigate(
                                KickifyRoute.ProductListWithCategory(brandName)
                            )
                        }
                    )
                }
            }

            // begin shoes section
            val homeSectionsModifier = Modifier.padding(vertical = 6.dp).padding(horizontal = 8.dp)
            HomeScreenSectionSquareProductCards(
                navController,
                modifier = homeSectionsModifier,
                sectionTitle = "Popular Shoes",
                prodList = mapOf("p1" to false, "p2" to false)
            )

            HomeScreenSectionSquareProductCards(
                navController,
                modifier = homeSectionsModifier,
                sectionTitle = "Novelties",
                prodList = mapOf("p1" to true)
            )

            HomeScreenSectionSquareProductCards(
                navController,
                modifier = homeSectionsModifier,
                sectionTitle = "Discounted",
                prodList = mapOf("p1" to false, "p2" to false)
            )
        }

    }
}