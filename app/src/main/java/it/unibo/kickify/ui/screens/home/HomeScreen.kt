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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.models.ShopCategory
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.HomeScreenCategory
import it.unibo.kickify.ui.composables.HomeScreenSection
import it.unibo.kickify.ui.composables.HomeScreenSmallBrandLogos
import it.unibo.kickify.ui.composables.SearchRoundedTextField

@Preview
@Composable
fun HomeScreen(
    navController: NavController = NavController(LocalContext.current)
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
            BottomBar()
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
                onSearchAction = {  }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp).padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                ShopCategory.entries.forEach {
                    HomeScreenCategory(it) { }
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
                for(brandName in brands){
                    HomeScreenSmallBrandLogos(brandName)
                }
            }

            // begin shoes section
            val homeSectionsModifier = Modifier.padding(vertical = 6.dp).padding(horizontal = 8.dp)
            HomeScreenSection(modifier = homeSectionsModifier,
                sectionTitle = "Popular Shoes",
                prodList = listOf("p1", "p2"))

            HomeScreenSection(modifier = homeSectionsModifier,
                sectionTitle = "Novelties",
                prodList = listOf("p1", "p2"))

            HomeScreenSection(modifier = homeSectionsModifier,
                sectionTitle = "Discounted",
                prodList = listOf("p1", "p2"))
        }

    }
}