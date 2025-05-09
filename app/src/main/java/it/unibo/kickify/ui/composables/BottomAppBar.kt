package it.unibo.kickify.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import it.unibo.kickify.ui.KickifyRoute

@Composable
fun BottomBar(navController: NavController){
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Spacer(Modifier.weight(0.7f, true))
        IconButton(onClick = {
            navController.navigate(KickifyRoute.Home) {
                popUpTo(KickifyRoute.Home) { inclusive = true }
            }
        } ) {
            Icon(Icons.Outlined.Home, contentDescription = "Home")
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = { navController.navigate(KickifyRoute.Wishlist) }) {
            Icon(Icons.Outlined.FavoriteBorder, contentDescription = "")
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = { navController.navigate(KickifyRoute.Cart ) }) {
            Icon(Icons.Outlined.ShoppingBag, contentDescription = "")
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = { navController.navigate(KickifyRoute.Notifications) }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "")
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = { navController.navigate(KickifyRoute.Profile) }) {
            Icon(Icons.Outlined.Person, contentDescription = "")
        }
        Spacer(Modifier.weight(0.7f, true))
    }
}