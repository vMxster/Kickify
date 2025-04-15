package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController, title: String) {
    CenterAlignedTopAppBar(
        title = {
            if(title == stringResource(R.string.app_name)){
                if(isSystemInDarkTheme()){
                    Image(
                        painterResource(R.drawable.kickify_dark_banner),
                        "logo",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.height(40.dp)
                    )
                } else {
                    Image(
                        painterResource(R.drawable.kickify_light_banner),
                        "logo",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.height(40.dp)
                    )
                }

            } else {
                Text(text = title,
                    fontWeight = FontWeight.Medium,
                )
            }
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Go Back")
                }
            } else {
                IconButton(onClick = {  }) {
                    Icon(Icons.Outlined.Menu, "Menu")
                }
            }
        },
        actions = {
            if (title == "Kickify") {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.ShoppingBag, contentDescription = "Cart")
                }
            }
            /*if (title != "Settings") {
                IconButton(onClick = { navController.navigate(Settings) }) {
                    Icon(Icons.Outlined.Settings, "Settings")
                }
            }*/
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}