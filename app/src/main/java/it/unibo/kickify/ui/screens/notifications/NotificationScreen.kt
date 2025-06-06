package it.unibo.kickify.ui.screens.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import it.unibo.kickify.R
import it.unibo.kickify.data.database.Notification
import it.unibo.kickify.data.models.NotificationType
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.NotificationItem
import it.unibo.kickify.ui.composables.NotificationTitleLine
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.theme.BluePrimary
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(
    navController: NavController,
    notificationViewModel: NotificationViewModel,
    settingsViewModel: SettingsViewModel
){
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoading by notificationViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by notificationViewModel.errorMessage.collectAsStateWithLifecycle()
    val notificationState by notificationViewModel.notificationState.collectAsStateWithLifecycle()
    val unreadNotifications by notificationViewModel.unreadNotifications.collectAsStateWithLifecycle()

    val email by settingsViewModel.userId.collectAsStateWithLifecycle()
    val lastAccess = "2025-06-01 10:02:41" //by settingsViewModel.lastAccess.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val notificationList = remember { mutableListOf<Notification>() }

    LaunchedEffect(notificationState, email) {
        notificationViewModel.getNotifications(email)
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.notificationscreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        snackBarHostState = snackBarHostState,
        showLoadingOverlay = isLoading
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if(errorMessage == null){
                for (n in notificationList){
                    NotificationTitleLine(n.date)
                    NotificationItem(
                        NotificationType.ProductBackinStock, //n.type
                        notificationText = "REMOTE:" + n.message,
                        colorDot = if(n.state == "Unread") BluePrimary else Color.Gray,
                        onClick = {
                            coroutineScope.launch {
                                notificationViewModel.markNotificationsAsRead(
                                    email = email,
                                    notificationIds = listOf(n.notificationId)
                                )
                                notificationViewModel.getNotifications(email)
                            }
                        }
                    )
                }
            } else {
                Text("an error occurred:\n$errorMessage")
            }


            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))

            NotificationTitleLine("11 April 2025")
            NotificationItem(
                NotificationType.ProductBackinStock,
                notificationText = "Great news, Mario: the Converse Chuck 70 High All Star is back in stock!!",
                colorDot = BluePrimary,
                // onClick = onClick
            ) { }
            NotificationItem(
                NotificationType.OrderShipped,
                notificationText = "Your order #609327855 was handed over to the SDA express courier...",
                colorDot = BluePrimary,
                // onClick = onClick
            ) { }
            NotificationItem(
                NotificationType.FlashSale,
                notificationText = "Your favourite product Adidas Spezial is noew 20% off for a short time, ...",
                colorDot = BluePrimary,
                // onClick = onClick
            ) { }

            NotificationTitleLine("10 April 2025")
            NotificationItem(
                NotificationType.ItemsinCart,
                notificationText = "You spend a lot of time browsing but just spend a minute to make them...",
                colorDot = BluePrimary,
                // onClick = onClick
            ) { }
            NotificationItem(
                NotificationType.OrderPlaced,
                notificationText = "Your payment has been succesfully processed and we are starting to...",
                colorDot = Color.Gray,
                // onClick = onClick
            ) { }
            NotificationItem(
                NotificationType.RequestedProductReview,
                notificationText = "Share your experience with the product you purchased!",
                colorDot = BluePrimary,
                // onClick = onClick
            ) { }
        }
    }
}