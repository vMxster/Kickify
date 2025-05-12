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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.models.ShopCategory
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.HomeScreenCategory
import it.unibo.kickify.ui.composables.HomeScreenSectionSquareProductCards
import it.unibo.kickify.ui.composables.HomeScreenSmallBrandLogos
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.composables.SearchRoundedTextField

@Composable
fun HomeScreen(
    navController: NavController,
){
    val brands = mapOf("Nike" to R.drawable.nike,
        "Puma" to R.drawable.puma, "Under Armour" to R.drawable.under_armour,
        "Adidas" to R.drawable.adidas, "Converse" to R.drawable.converse)

    ScreenTemplate(
        screenTitle = stringResource(R.string.app_name),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
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
                for((name, id) in brands.entries){
                    val brand = stringResource(R.string.brand)
                    HomeScreenSmallBrandLogos(
                        name = name,
                        brandResLogoID = id,
                        onClick = {
                            navController.navigate(
                                KickifyRoute.ProductListWithCategory("$brand: $name")
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
                sectionTitle = stringResource(R.string.homescreen_popular),
                prodList = mapOf("p1" to false, "p2" to false)
            )

            HomeScreenSectionSquareProductCards(
                navController,
                modifier = homeSectionsModifier,
                sectionTitle = stringResource(R.string.homescreen_novelties),
                prodList = mapOf("p1" to true)
            )

            HomeScreenSectionSquareProductCards(
                navController,
                modifier = homeSectionsModifier,
                sectionTitle = stringResource(R.string.homescreen_discounted),
                prodList = mapOf("p1" to false, "p2" to false)
            )
        }

    }
}