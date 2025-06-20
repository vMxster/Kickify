package it.unibo.kickify.ui.screens.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.models.NotificationType
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.NotificationItem
import it.unibo.kickify.ui.composables.NotificationTitleLine
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.theme.BluePrimary
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NotificationScreen(
    navController: NavController,
    notificationViewModel: NotificationViewModel,
    settingsViewModel: SettingsViewModel,
    achievementsViewModel: AchievementsViewModel
){
    val isLoading by notificationViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by notificationViewModel.errorMessage.collectAsStateWithLifecycle()
    val notificationState by notificationViewModel.notificationState.collectAsStateWithLifecycle()
    val notificationList = notificationState ?: listOf()

    val email by settingsViewModel.userId.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(notificationState, email) {
        notificationViewModel.getNotifications(email)
    }

    fun convertDateFormat(dateString: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(dateString, inputFormatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return dateTime.format(outputFormatter)
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.notificationscreen_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        showLoadingOverlay = isLoading,
        achievementsViewModel = achievementsViewModel
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // extract the product code inside notification to navigate to that product's page
            fun extractNumberAndGetString(inputString: String): Pair<Int?, String> {
                val startIndex = inputString.indexOf('[')
                val endIndex = inputString.indexOf(']')

                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    val numberString = inputString.substring(startIndex + 1, endIndex)
                    val number = numberString.toIntOrNull()

                    val cleanedString = inputString.substring(0, startIndex) +
                            inputString.substring(endIndex + 1)

                    return Pair(number, cleanedString.trim())
                }
                return Pair(null, inputString)
            }

            val notificationTypesGoToProductPageOnClick = listOf(
                NotificationType.ProductBackinStock, NotificationType.FlashSale, NotificationType.RequestedProductReview
            )
            val notificationTypesGoToOrderPageOnClick = listOf(
                NotificationType.OrderPlaced, NotificationType.OrderShipped, NotificationType.OrderDelivered
            )

            if(errorMessage == null){
                val notificationGrouped = notificationList.groupBy { convertDateFormat(it.date) }
                for ((date, notifications) in notificationGrouped.entries){
                    NotificationTitleLine(date)
                    for(n in notifications) {
                        val updatedMsg = extractNumberAndGetString(n.message)
                        val codeInNotification = updatedMsg.first
                        val msg = updatedMsg.second
                        val notificationType = NotificationType.getTypeFromString(n.type, n.message)
                        NotificationItem(
                            notificationType = notificationType,
                            notificationText = msg,
                            colorDot = if (n.state == "Unread") BluePrimary else Color.Gray,
                            onClick = {
                                coroutineScope.launch {
                                    notificationViewModel.markNotificationsAsRead(
                                        email = email,
                                        notificationIds = listOf(n.notificationId)
                                    )
                                    notificationViewModel.getNotifications(email)
                                    if(notificationType in notificationTypesGoToProductPageOnClick
                                        && codeInNotification != null){
                                        navController.navigate(KickifyRoute.ProductDetails(codeInNotification))
                                    }
                                    if(notificationType in notificationTypesGoToOrderPageOnClick
                                        && codeInNotification != null){
                                        navController.navigate(KickifyRoute.OrderDetails(codeInNotification))
                                    }
                                }
                            }
                        )
                    }
                }
            } else {
                Text("an error occurred:\n$errorMessage")
            }
        }
    }
}