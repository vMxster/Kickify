package it.unibo.kickify.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.screens.cart.CartViewModel
import it.unibo.kickify.ui.screens.notifications.NotificationViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScreenTemplate(
    screenTitle: String,
    navController: NavController,
    showTopAppBar: Boolean,
    bottomAppBarContent: @Composable () -> Unit = { },
    showModalDrawer: Boolean,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    showLoadingOverlay: Boolean = false,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val notificationViewModel = koinViewModel<NotificationViewModel>()
    val unreadNotifications by notificationViewModel.unreadNotifications.collectAsStateWithLifecycle()

    val cartViewModel = koinViewModel<CartViewModel>()
    val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()

    val scaffoldContent: @Composable () -> Unit = {
        Scaffold(
            topBar = {
                if(showTopAppBar) {
                    AppBar(
                        navController,
                        title = screenTitle,
                        onNavigationMenuClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            } else {
                                coroutineScope.launch { drawerState.open() }
                            }
                        },
                        unreadNotifications
                    )
                }
            },
            bottomBar = { bottomAppBarContent() },
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                content() // screen content

                AnimatedVisibility(
                    visible = showLoadingOverlay,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(enabled = false) {}
                            .wrapContentSize(Alignment.Center)
                    ) {
                        CircularProgressIndicator(
                            //color = MaterialTheme.colorScheme.o
                        )
                    }
                }
            }
        }
    }

    if(showModalDrawer) { // show modal with scaffold content only when requested
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerState) {
                    SideMenuContent{

                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        fun isSelectedIcon(route : KickifyRoute) : Boolean{
                            return currentDestination?.hasRoute(route::class) ?: false
                        }

                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.home)) },
                            selected = isSelectedIcon(KickifyRoute.Home),
                            onClick = {
                                if(!isSelectedIcon(KickifyRoute.Home)){
                                    navController.navigate(KickifyRoute.Home) {
                                        popUpTo(KickifyRoute.Home) { inclusive = true }
                                    }
                                }
                            },
                            icon = { Icon(Icons.Outlined.Home, contentDescription = null)  }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.wishlist_title)) },
                            selected = isSelectedIcon(KickifyRoute.Wishlist),
                            onClick = { navController.navigate(KickifyRoute.Wishlist) },
                            icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = null) },
                            badge = { SideMenuSimpleCounter(20) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.cart)) },
                            selected = isSelectedIcon(KickifyRoute.Cart),
                            onClick = { navController.navigate(KickifyRoute.Cart) },
                            icon = { Icon(Icons.Outlined.ShoppingBag, contentDescription = null) },
                            badge = { SideMenuSimpleCounter(cartItems.size) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.notificationscreen_title)) },
                            selected = isSelectedIcon(KickifyRoute.Notifications),
                            onClick = { navController.navigate(KickifyRoute.Notifications) },
                            icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                            badge = { SideMenuSimpleCounter(unreadNotifications) }
                        )

                        HorizontalDivider(Modifier.padding(vertical = 12.dp))

                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.profileScreen_title)) },
                            selected = isSelectedIcon(KickifyRoute.Profile),
                            onClick = { navController.navigate(KickifyRoute.Profile) },
                            icon = { Icon(Icons.Outlined.Person, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.myAchievements)) },
                            selected = isSelectedIcon(KickifyRoute.Achievements),
                            onClick = { navController.navigate(KickifyRoute.Achievements) },
                            icon = { Icon(Icons.Outlined.EmojiEvents, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.myordersScreen_title)) },
                            selected = isSelectedIcon(KickifyRoute.MyOrders),
                            onClick = { navController.navigate(KickifyRoute.MyOrders) },
                            icon = { Icon(Icons.Outlined.LocalShipping, contentDescription = null) }
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.settings_title)) },
                            selected = isSelectedIcon(KickifyRoute.Settings),
                            onClick = { navController.navigate(KickifyRoute.Settings) },
                            icon = { Icon(Icons.Outlined.Settings, contentDescription = null) }
                        )
                    }
                }
            },
            content = { scaffoldContent() }
        )
    } else {
        scaffoldContent()
    }
}

@Composable
fun SideMenuSimpleCounter(count: Int){
    if(count > 0){
        Text("$count")
    }
}

@Composable
fun SideMenuContent(navigationDrawerItems: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.app_name),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )

        HorizontalDivider(Modifier.padding(bottom = 12.dp))

        navigationDrawerItems()
    }
}