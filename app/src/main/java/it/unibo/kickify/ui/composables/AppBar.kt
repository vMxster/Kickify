package it.unibo.kickify.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.screens.products.FilterScreen
import it.unibo.kickify.ui.screens.products.FilterState
import it.unibo.kickify.ui.theme.BluePrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavController,
    title: String = "",
    onNavigationMenuClick: () -> Unit,
    unreadNotificationsCount: Int,
    markAllNotificationsAsRead: () -> Unit,
    isInWishlist: Boolean = false,
    onToggleWishlist: (() -> Unit)? = null,
    onApplyFilter: ((FilterState) -> Unit)? = null,
    onResetFilter: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            if(title == stringResource(R.string.app_name)) {
                Image(
                    painterResource(R.drawable.kickify_light_banner),
                    "logo",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.height(64.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )

            } else if (title != "Details"){
                Text(text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },

        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = {
                    onResetFilter?.invoke()
                    navController.navigateUp()
                }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Go Back")
                }
            } else if(title != ""){
                IconButton(onClick = { onNavigationMenuClick() }) {
                    Icon(Icons.Outlined.Menu, "Menu")
                }
            }
        },

        actions = {
            if(title == stringResource(R.string.app_name)){
                IconButton(
                    onClick = { navController.navigate(KickifyRoute.Notifications){ launchSingleTop = true } }
                ) {
                    CustomBadge(
                        badgeCount = unreadNotificationsCount,
                        contentDescr = "$unreadNotificationsCount notifications to read",
                        modifier = Modifier.offset(x = (-4).dp, y = (-4).dp)
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = stringResource(R.string.notificationscreen_title)
                        )
                    }
                }
            }

            if(title == stringResource(R.string.notificationscreen_title)){
                TextButton(onClick = { markAllNotificationsAsRead() }) {
                    Text(
                        text = stringResource(R.string.notificationscreen_markAllRead),
                        color = BluePrimary
                    )
                }
            }

            // show filter button only on pages that show a list of shoes
            if(title == stringResource(R.string.homescreen_popular)
                || title == stringResource(R.string.homescreen_novelties)
                || title == stringResource(R.string.homescreen_discounted)
                || title.contains(stringResource(R.string.shopCategory_men))
                || title.contains(stringResource(R.string.shopCategory_women))
                || title.contains(stringResource(R.string.shopCategory_kids))
                || title.startsWith(stringResource(R.string.brand))
                || title == stringResource(R.string.exploreShoes)
            ){
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            showSheet = true
                            sheetState.show()
                        }
                    }
                ) {
                    Icon(Icons.Outlined.Tune, contentDescription = "Filter")
                }
            }
            if(showSheet){
                FilterScreen(
                    onDismissRequest = {
                        coroutineScope.launch {
                            showSheet = false
                            sheetState.hide()
                        }
                    },
                    sheetState = sheetState,
                    initialFilterState = FilterState(),
                    onApplyFilter = { newFilterState ->
                        onApplyFilter?.invoke(newFilterState)
                        coroutineScope.launch {
                            showSheet = false
                            sheetState.hide()
                        }
                    },
                    onResetFilter = { onResetFilter?.invoke() }
                )
            }

            if (title == stringResource(R.string.profileScreen_title)){
                IconButton(onClick = { navController.navigate(KickifyRoute.Achievements) }) {
                    Icon(Icons.Outlined.EmojiEvents, contentDescription = "View achievements")
                }
                IconButton(onClick = { navController.navigate(KickifyRoute.Settings) }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Edit app settings")
                }
            }

            if(title == "Details"){
                println("Stato attuale wishlist: $isInWishlist")

                IconButton(
                    onClick = {
                        println("Click sul pulsante wishlist")
                        onToggleWishlist?.invoke()
                    }
                ) {
                    Icon(
                        imageVector = if (isInWishlist) Icons.Outlined.Favorite
                        else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isInWishlist)
                            "Rimuovi dai preferiti"
                        else "Aggiungi ai preferiti",
                        tint = Color.Red
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}