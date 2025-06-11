package it.unibo.kickify.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.screens.cart.CartViewModel
import it.unibo.kickify.ui.screens.wishlist.WishlistViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BottomBar(
    navController: NavController
){
    val wishlistViewModel = koinViewModel<WishlistViewModel>()
    val wishlistItems by wishlistViewModel.wishlistState.collectAsStateWithLifecycle()

    val cartViewModel = koinViewModel<CartViewModel>()
    val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()

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
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            onClick = {
                if(!isSelectedIcon(KickifyRoute.Home)){
                    navController.navigate(KickifyRoute.Home) {
                        popUpTo(KickifyRoute.Home) { inclusive = true }
                    }
                }
            },
            label = { Text(stringResource(R.string.home), textAlign = TextAlign.Center) }
        )

        NavigationBarItem(
            selected = isSelectedIcon(KickifyRoute.Wishlist),
            icon = {
                CustomBadge(
                    badgeCount = wishlistItems.size,
                    contentDescr = "${wishlistItems.size} items in cart"
                ) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = null)
                }
            },
            onClick = {
                if(!isSelectedIcon(KickifyRoute.Wishlist)){
                    navController.navigate(KickifyRoute.Wishlist)
                }
            },
            label = { Text(stringResource(R.string.wishlist_title), textAlign = TextAlign.Center) }
        )

        NavigationBarItem(
            selected = isSelectedIcon(KickifyRoute.Cart),
            icon = {
                CustomBadge(
                    badgeCount = cartItems.size,
                    contentDescr = "${cartItems.size} items in cart"
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
            label = { Text(stringResource(R.string.cart), textAlign = TextAlign.Center) }
        )

        NavigationBarItem(
            selected = isSelectedIcon(KickifyRoute.Profile),
            icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            onClick = {
                if(!isSelectedIcon(KickifyRoute.Profile)){
                    navController.navigate(KickifyRoute.Profile)
                }
            },
            label = { Text(stringResource(R.string.profileScreen_title), textAlign = TextAlign.Center) }
        )
    }
}

@Composable
fun CustomBadge(
    badgeCount: Int,
    contentDescr: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit){
    BadgedBox(
        badge = {
            if(badgeCount > 0) {
                Badge(
                    modifier = modifier
                ) {
                    Text( if(badgeCount <= 9) "$badgeCount" else "9+",
                        modifier = Modifier.semantics {
                            contentDescription = contentDescr
                        }
                    )
                }
            }
        }
    ){ content() }
}