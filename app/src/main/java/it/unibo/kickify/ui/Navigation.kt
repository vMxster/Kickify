package it.unibo.kickify.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import it.unibo.kickify.camerax.CameraXUtils
import it.unibo.kickify.ui.screens.achievements.AchievementsScreen
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.cart.CartScreen
import it.unibo.kickify.ui.screens.cart.CartViewModel
import it.unibo.kickify.ui.screens.checkout.CheckOutScreen
import it.unibo.kickify.ui.screens.forgotPassword.ForgotPasswordOTPViewModel
import it.unibo.kickify.ui.screens.forgotPassword.ForgotPasswordScreen
import it.unibo.kickify.ui.screens.forgotPassword.OTPScreen
import it.unibo.kickify.ui.screens.forgotPassword.ResetPasswordScreen
import it.unibo.kickify.ui.screens.home.HomeScreen
import it.unibo.kickify.ui.screens.login.BiometricLoginScreen
import it.unibo.kickify.ui.screens.login.LoginScreen
import it.unibo.kickify.ui.screens.login.LoginViewModel
import it.unibo.kickify.ui.screens.notifications.NotificationScreen
import it.unibo.kickify.ui.screens.notifications.NotificationViewModel
import it.unibo.kickify.ui.screens.onboard.OnboardingScreen
import it.unibo.kickify.ui.screens.orders.MyOrdersScreen
import it.unibo.kickify.ui.screens.orders.OrderDetailsScreen
import it.unibo.kickify.ui.screens.orders.OrdersViewModel
import it.unibo.kickify.ui.screens.products.ProductDetailsScreen
import it.unibo.kickify.ui.screens.products.ProductListScreen
import it.unibo.kickify.ui.screens.products.ProductsViewModel
import it.unibo.kickify.ui.screens.profile.EditProfileScreen
import it.unibo.kickify.ui.screens.profile.EditProfileSections
import it.unibo.kickify.ui.screens.profile.ProfileScreen
import it.unibo.kickify.ui.screens.profile.ProfileViewModel
import it.unibo.kickify.ui.screens.profile.TakePhotoScreen
import it.unibo.kickify.ui.screens.register.RegisterScreen
import it.unibo.kickify.ui.screens.settings.SettingsScreen
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.screens.wishlist.WishlistScreen
import it.unibo.kickify.ui.screens.wishlist.WishlistViewModel
import it.unibo.kickify.ui.theme.BluePrimary
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

enum class AppStartDestination  {
    LOADING,        // wait when loading settings
    ONBOARDING,     // first time app start
    LOGIN,          // user can log in
    BIOMETRIC_AUTH, // user can authenticate using biometry
    HOME            // user is logged in and can go to home screen
}

sealed interface KickifyRoute {
    @Serializable data object Home : KickifyRoute
    @Serializable data object Cart : KickifyRoute
    @Serializable data object Checkout : KickifyRoute
    @Serializable data object Login : KickifyRoute
    @Serializable data object ForgotPassword : KickifyRoute
    @Serializable data object OTPScreen : KickifyRoute
    @Serializable data object ResetPasswordScreen: KickifyRoute
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
    @Serializable data class OrderDetails(val orderID: Int) : KickifyRoute
    @Serializable data object TakeProfilePhoto : KickifyRoute
    @Serializable data object Achievements : KickifyRoute
    @Serializable data class EditProfile(val section: EditProfileSections): KickifyRoute
    @Serializable data object BiometricLogin: KickifyRoute
}

@Composable
fun KickifyNavGraph(
    navController: NavHostController,
    activity: ComponentActivity,
    settingsViewModel: SettingsViewModel
) {
    val cameraXUtils =  koinInject<CameraXUtils>()

    val wishlistViewModel = koinViewModel<WishlistViewModel>()
    val productsViewModel = koinViewModel<ProductsViewModel>()
    val achievementsViewModel = koinViewModel<AchievementsViewModel>()
    val loginViewModel = koinViewModel<LoginViewModel>()
    val notificationViewModel = koinViewModel<NotificationViewModel>()
    val forgotPasswordOTPViewModel = koinViewModel<ForgotPasswordOTPViewModel>()
    val ordersViewModel = koinViewModel<OrdersViewModel>()
    val cartViewModel = koinViewModel<CartViewModel>()
    val profileViewModel = koinViewModel<ProfileViewModel>()

    val startDestination by settingsViewModel.startDestination.collectAsStateWithLifecycle()

    if (startDestination == AppStartDestination.LOADING) {
        Box(modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BluePrimary)
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = when (startDestination) {
            AppStartDestination.ONBOARDING -> KickifyRoute.Onboard
            AppStartDestination.LOGIN -> KickifyRoute.Login
            AppStartDestination.BIOMETRIC_AUTH -> KickifyRoute.BiometricLogin
            AppStartDestination.HOME -> KickifyRoute.Home
            else -> KickifyRoute.Onboard
        }
    ) {
        composable<KickifyRoute.Home> {
            HomeScreen(navController, productsViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.Cart> {
            CartScreen(navController, cartViewModel, productsViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.Checkout> {
            CheckOutScreen(navController, achievementsViewModel, profileViewModel)
        }

        composable<KickifyRoute.Login> {
            LoginScreen(
                navController = navController,
                settingsViewModel = settingsViewModel,
                loginViewModel = loginViewModel,
                achievementsViewModel = achievementsViewModel,
                onLoginSuccess = {
                    achievementsViewModel.achieveAchievement(1)
                    navController.navigate(KickifyRoute.Home) {
                        popUpTo(KickifyRoute.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<KickifyRoute.ForgotPassword> {
            ForgotPasswordScreen(navController, forgotPasswordOTPViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.OTPScreen> {
            OTPScreen(navController, forgotPasswordOTPViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.ResetPasswordScreen> {
            ResetPasswordScreen(navController, forgotPasswordOTPViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.Notifications> {
            NotificationScreen(navController, notificationViewModel, settingsViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.Onboard> {
            OnboardingScreen(
                navController,
                achievementsViewModel,
                onReachedLastPage = {
                    navController.navigate(KickifyRoute.Login) {
                        popUpTo(KickifyRoute.Onboard) { inclusive = true }
                    }
                }
            )
        }

        composable<KickifyRoute.ProductDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<KickifyRoute.ProductDetails>()
            ProductDetailsScreen(navController, productsViewModel, cartViewModel,
                achievementsViewModel, route.productId)
        }

        composable<KickifyRoute.ProductList> {
            ProductListScreen(navController, productsViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.ProductListWithCategory> { backStackEntry ->
            val route = backStackEntry.toRoute<KickifyRoute.ProductListWithCategory>()
            ProductListScreen(navController, productsViewModel, achievementsViewModel, route.category)
        }

        composable<KickifyRoute.Profile> {
            ProfileScreen(navController, settingsViewModel, profileViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.Register> {
            RegisterScreen(navController, settingsViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.Settings> {
            SettingsScreen(navController, settingsViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.Wishlist> {
            WishlistScreen(navController, settingsViewModel, wishlistViewModel, productsViewModel,
                achievementsViewModel)
        }

        composable<KickifyRoute.MyOrders> {
            MyOrdersScreen(navController, settingsViewModel, ordersViewModel, achievementsViewModel)
        }

        composable<KickifyRoute.OrderDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<KickifyRoute.OrderDetails>()
            OrderDetailsScreen(navController, achievementsViewModel, route.orderID.toString())
        }

        composable<KickifyRoute.TakeProfilePhoto> {
            TakePhotoScreen(navController, activity, cameraXUtils, settingsViewModel)
        }

        composable<KickifyRoute.Achievements> {
            AchievementsScreen(navController, achievementsViewModel)
        }

        composable<KickifyRoute.EditProfile> { backStackEntry ->
            val route = backStackEntry.toRoute<KickifyRoute.EditProfile>()
            EditProfileScreen(navController, route.section, cameraXUtils, settingsViewModel,
                achievementsViewModel)
        }

        composable<KickifyRoute.BiometricLogin> {
            BiometricLoginScreen(navController, achievementsViewModel)
        }
    }
}
