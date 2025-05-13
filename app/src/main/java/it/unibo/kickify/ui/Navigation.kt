package it.unibo.kickify.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import it.unibo.kickify.camerax.CameraXutils
import it.unibo.kickify.ui.screens.cart.CartScreen
import it.unibo.kickify.ui.screens.checkout.CheckOutScreen
import it.unibo.kickify.ui.screens.home.HomeScreen
import it.unibo.kickify.ui.screens.login.ForgotPasswordScreen
import it.unibo.kickify.ui.screens.login.LoginScreen
import it.unibo.kickify.ui.screens.login.OTPScreen
import it.unibo.kickify.ui.screens.notifications.NotificationScreen
import it.unibo.kickify.ui.screens.onboard.OnboardingScreen
import it.unibo.kickify.ui.screens.orders.MyOrdersScreen
import it.unibo.kickify.ui.screens.orders.OrderDetailsScreen
import it.unibo.kickify.ui.screens.productDetails.ProductDetailsScreen
import it.unibo.kickify.ui.screens.productList.ProductListScreen
import it.unibo.kickify.ui.screens.profile.ProfileScreen
import it.unibo.kickify.ui.screens.profile.TakePhotoScreen
import it.unibo.kickify.ui.screens.register.RegisterScreen
import it.unibo.kickify.ui.screens.settings.SettingsScreen
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.screens.wishlist.WishlistScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

sealed interface KickifyRoute {
    @Serializable data object Home : KickifyRoute
    @Serializable data object Cart : KickifyRoute
    @Serializable data object Checkout : KickifyRoute
    @Serializable data object Login : KickifyRoute
    @Serializable data object ForgotPassword : KickifyRoute
    @Serializable data object OTPScreen : KickifyRoute
    @Serializable data object Notifications : KickifyRoute
    @Serializable data object Onboard : KickifyRoute
    @Serializable data class ProductDetails(val productId: Int) : KickifyRoute
    @Serializable data object ProductList : KickifyRoute
    @Serializable data class ProductListWithCategory(val category: String) : KickifyRoute
    @Serializable data object Profile : KickifyRoute
    @Serializable data object Register : KickifyRoute
    @Serializable data object Settings : KickifyRoute
    @Serializable data object Wishlist : KickifyRoute
    @Serializable data object MyOrders : KickifyRoute
    @Serializable data class OrderDetails(val orderID: String) : KickifyRoute
    @Serializable data object TakeProfilePhoto : KickifyRoute
}

@Composable
fun KickifyNavGraph(
    navController: NavHostController,
    activity: ComponentActivity,
    settingsViewModel: SettingsViewModel
) {
    val cameraXutils =  koinInject<CameraXutils>()

    NavHost(
        navController = navController,
        startDestination = KickifyRoute.Onboard
    ) {
        composable<KickifyRoute.Home> {
            HomeScreen(navController)
        }

        composable<KickifyRoute.Cart> {
            CartScreen(navController)
        }

        composable<KickifyRoute.Checkout> {
            CheckOutScreen(navController)
        }

        composable<KickifyRoute.Login> {
            LoginScreen(navController)
        }

        composable<KickifyRoute.ForgotPassword> {
            ForgotPasswordScreen(navController)
        }

        composable<KickifyRoute.OTPScreen> {
            OTPScreen(navController)
        }

        composable<KickifyRoute.Notifications> {
            NotificationScreen(navController)
        }

        composable<KickifyRoute.Onboard> {
            OnboardingScreen(navController,
                onReachedLastPage = {
                    navController.navigate(KickifyRoute.Login) {
                        popUpTo(KickifyRoute.Onboard) { inclusive = true }
                    }
                }
            )
        }

        composable<KickifyRoute.ProductDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<KickifyRoute.ProductDetails>()
            ProductDetailsScreen(navController, route.productId)
        }

        composable<KickifyRoute.ProductList> {
            ProductListScreen(navController)
        }

        composable<KickifyRoute.ProductListWithCategory> { backStackEntry ->
            val route = backStackEntry.toRoute<KickifyRoute.ProductListWithCategory>()
            ProductListScreen(navController, route.category)
        }

        composable<KickifyRoute.Profile> {
            ProfileScreen(navController, cameraXutils, settingsViewModel)
        }

        composable<KickifyRoute.Register> {
            RegisterScreen(navController)
        }

        composable<KickifyRoute.Settings> {
            SettingsScreen(navController, settingsViewModel)
        }

        composable<KickifyRoute.Wishlist> {
            WishlistScreen(navController)
        }

        composable<KickifyRoute.MyOrders> {
            MyOrdersScreen(navController)
        }

        composable<KickifyRoute.OrderDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<KickifyRoute.OrderDetails>()
            OrderDetailsScreen(navController, route.orderID)
        }

        composable<KickifyRoute.TakeProfilePhoto> {
            TakePhotoScreen(navController, activity, cameraXutils, settingsViewModel)
        }
    }
}