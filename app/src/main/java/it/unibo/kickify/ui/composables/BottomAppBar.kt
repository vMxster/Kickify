package it.unibo.kickify.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute

@Composable
fun BottomBar(
    navController: NavController,
){
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        fun isSelectedIcon(route : KickifyRoute) : Boolean{
            return currentDestination?.hasRoute(route::class) ?: false
        }

        NavigationBarItem(
            selected = isSelectedIcon(KickifyRoute.Home),
            icon = {
                Icon(Icons.Outlined.Home, contentDescription = stringResource(R.string.home))
            },
            onClick = {
                if(!isSelectedIcon(KickifyRoute.Home)){
                    navController.navigate(KickifyRoute.Home) {
                        popUpTo(KickifyRoute.Home) { inclusive = true }
                    }
                }
            },
            label = { Text(stringResource(R.string.home)) }
        )

        NavigationBarItem(
            selected = isSelectedIcon(KickifyRoute.Wishlist),
            icon = {
                Icon(
                    Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(R.string.wishlist_title)
                )
            },
            onClick = {
                if(!isSelectedIcon(KickifyRoute.Wishlist)){
                    navController.navigate(KickifyRoute.Wishlist)
                }
            },
            label = { Text(stringResource(R.string.wishlist_title)) }
        )

        val cartItems by rememberSaveable { mutableIntStateOf(11) }
        NavigationBarItem(
            selected = isSelectedIcon(KickifyRoute.Cart),
            icon = {
                CustomBadge(
                    badgeCount = cartItems,
                    contentDescr = "$cartItems items in cart"
                ){
                    Icon(
                        Icons.Outlined.ShoppingBag,
                        contentDescription = stringResource(R.string.cart)
                    )
                }
            },
            onClick = {
                if(!isSelectedIcon(KickifyRoute.Cart)){
                    navController.navigate(KickifyRoute.Cart)
                }
            },
            label = { Text(stringResource(R.string.cart)) }
        )

        val notificationCount by rememberSaveable { mutableIntStateOf(3) }
        NavigationBarItem(
            selected = isSelectedIcon(KickifyRoute.Notifications),
            icon = {
                CustomBadge(
                    badgeCount = notificationCount,
                    contentDescr = "$notificationCount notifications to read"
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = stringResource(R.string.notificationscreen_title)
                    )
                }
            },
            onClick = {
                if(!isSelectedIcon(KickifyRoute.Notifications)){
                    navController.navigate(KickifyRoute.Notifications)
                }
            },
            label = { Text(stringResource(R.string.notificationscreen_title)) }
        )

        NavigationBarItem(
            selected = isSelectedIcon(KickifyRoute.Profile),
            icon = {
                Icon(Icons.Outlined.Person, contentDescription = stringResource(R.string.profileScreen_title))
            },
            onClick = {
                if(!isSelectedIcon(KickifyRoute.Profile)){
                    navController.navigate(KickifyRoute.Profile)
                }
            },
            label = { Text(stringResource(R.string.profileScreen_title)) }
        )
    }
}

@Composable
fun CustomBadge(
    badgeCount: Int,
    contentDescr: String,
    content: @Composable () -> Unit){
    BadgedBox(
        badge = {
            if(badgeCount > 0) {
                Badge {
                    Text( if(badgeCount <= 10) "$badgeCount" else "10+",
                        modifier = Modifier.semantics {
                            contentDescription = contentDescr
                        }
                    )
                }
            }
        }
    ){ content() }
}