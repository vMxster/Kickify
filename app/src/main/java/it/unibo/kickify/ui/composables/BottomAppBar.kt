package it.unibo.kickify.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.unibo.kickify.ui.theme.GhostWhite

@Composable
fun BottomBar(){
    BottomAppBar(
        containerColor = GhostWhite
    ) {
        Spacer(Modifier.weight(0.7f, true))
        IconButton(onClick = { /* go to Home */ }) {
            Icon(Icons.Outlined.Home, contentDescription = "Home")
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = { /* go to wishlist */ }) {
            Icon(Icons.Outlined.FavoriteBorder, contentDescription = "")
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = { /* go to search page */ }) {
            Icon(Icons.Outlined.Search, contentDescription = "")
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = { /* go to notification page */ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "")
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = { /* go to profile page */ }) {
            Icon(Icons.Outlined.Person, contentDescription = "")
        }
        Spacer(Modifier.weight(0.7f, true))
    }
}