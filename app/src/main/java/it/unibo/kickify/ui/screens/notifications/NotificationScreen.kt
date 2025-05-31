package it.unibo.kickify.ui.screens.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.NotificationItem
import it.unibo.kickify.ui.composables.NotificationTitleLine
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.theme.BluePrimary
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun NotificationScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel
){
    val snackBarHostState = remember { SnackbarHostState() }
    val appRepo = koinInject<AppRepository>()
    val userid by settingsViewModel.userId.collectAsStateWithLifecycle()
    val lastAccess = "2025-05-26" //by settingsViewModel.lastAccess.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val notificationList = remember { mutableListOf<Notification>() }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val res = appRepo.getNotifications(userid, lastAccess)
            if(res.isSuccess){
                res.getOrNull()?.let { notificationList.addAll(it) }
                snackBarHostState.showSnackbar(message = "result success",
                    duration = SnackbarDuration.Long)
            } else {
                snackBarHostState.showSnackbar(
                    message = "res.failure: $res"
                )
            }
        }
    }
    ScreenTemplate(
        screenTitle = stringResource(R.string.notificationscreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        snackBarHostState = snackBarHostState
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            for (n in notificationList){
                NotificationTitleLine(n.date)
                NotificationItem(
                    NotificationType.ProductBackinStock, //n.type
                    notificationText = "REMOTE:" + n.message,
                    colorDot = if(n.state == "Unread") BluePrimary else Color.Gray,
                    onClick = {
                        coroutineScope.launch {
                            appRepo.markNotificationsAsRead(
                                email = "",
                                notificationIds = listOf(n.notificationId))
                        }
                    }
                )
            }

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