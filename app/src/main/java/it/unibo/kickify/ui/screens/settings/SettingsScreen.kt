package it.unibo.kickify.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.PushNotificationManager
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.composables.SettingsItemWithLeadingIcon
import it.unibo.kickify.ui.composables.SettingsItemWithTrailingSwitchButton
import it.unibo.kickify.ui.composables.SettingsTitleLine
import it.unibo.kickify.ui.composables.ThemeChooserRow
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val themeState by settingsViewModel.theme.collectAsStateWithLifecycle()
    val biometricLoginState by settingsViewModel.biometricLogin.collectAsStateWithLifecycle()
    val pushNotificationState by settingsViewModel.enabledPushNotification.collectAsStateWithLifecycle()
    val pushNotificationManager = koinInject<PushNotificationManager>()

    ScreenTemplate(
        screenTitle = stringResource(R.string.settings_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            var showDialogLogout by rememberSaveable { mutableStateOf(false) }
            var showDialogDeleteAccount by rememberSaveable { mutableStateOf(false) }

            SettingsTitleLine(stringResource(R.string.settings_accountTitle))
            SettingsItemWithLeadingIcon(
                icon = Icons.Outlined.NotificationsActive,
                text = stringResource(R.string.settings_notifications),
                onClick = {}
            )
            SettingsItemWithLeadingIcon(
                icon = Icons.Outlined.ShoppingCart,
                text = stringResource(R.string.settings_shippingAddress),
                onClick = {}
            )
            SettingsItemWithLeadingIcon(
                icon = Icons.Outlined.AccountBalanceWallet,
                text = stringResource(R.string.payment),
                onClick = {}
            )
            SettingsItemWithLeadingIcon(
                icon = Icons.AutoMirrored.Outlined.Logout,
                text = stringResource(R.string.logout),
                onClick = { showDialogLogout = true }
            )
            SettingsItemWithLeadingIcon(
                icon = Icons.Outlined.Delete,
                text = stringResource(R.string.settings_deleteAccount),
                onClick = { showDialogDeleteAccount = true }
            )
            MessageDialog(
                showDialog = showDialogLogout,
                title = stringResource(R.string.logout),
                titleColor = null,
                text = stringResource(R.string.logout_confirmMessage),
                cancelText = stringResource(R.string.settings_cancel),
                confirmText = stringResource(R.string.logout),
                onConfirm = {
                    showDialogLogout = false
                    /* TODO delete settings view model */
                    navController.navigate(KickifyRoute.Login){
                        launchSingleTop = true
                        popUpTo(KickifyRoute.Home){ inclusive = true }
                    }
                },
                onDismiss = { showDialogLogout = false}
            )
            MessageDialog(
                showDialog = showDialogDeleteAccount,
                title = stringResource(R.string.caution),
                titleColor = Color.Red,
                text = stringResource(R.string.settings_deleteAccountConfirmMessage),
                cancelText = stringResource(R.string.settings_cancel),
                confirmText = stringResource(R.string.settings_deleteAccount),
                onConfirm = {
                    showDialogDeleteAccount = false
                    /* TODO delete settings view model */
                    navController.navigate(KickifyRoute.Login){
                        launchSingleTop = true
                        popUpTo(KickifyRoute.Home){ inclusive = true }
                    }
                },
                onDismiss = { showDialogDeleteAccount = false}
            )

            Spacer(Modifier.height(15.dp))
            SettingsTitleLine(stringResource(R.string.settings_appSettingsTitle))

            SettingsItemWithTrailingSwitchButton(
                enabled = settingsViewModel.isStrongAuthenticationAvailable(LocalContext.current),
                textIfDisabled = stringResource(R.string.settings_biometricLoginUnavailable),
                text = stringResource(R.string.settings_enableBiometricLogin),
                checked = biometricLoginState,
                onSwitchChange = {
                    settingsViewModel.setBiometricLogin(it)
                }
            )
            SettingsItemWithTrailingSwitchButton(
                enabled = pushNotificationManager.isPermissionGranted(),
                textIfDisabled = stringResource(R.string.settings_pushNotificationPermissionNotGranted),
                text = stringResource(R.string.settings_enablePushNotifications),
                checked = pushNotificationState,
                onSwitchChange = {
                    settingsViewModel.setEnabledPushNotification(it)
                }
            )

            ThemeChooserRow(
                selectedTheme = themeState,
                onThemeSelected = {
                    settingsViewModel.setTheme(it)
                }
            )
        }
    }
}

@Composable
fun MessageDialog(
    showDialog: Boolean,
    title: String,
    titleColor: Color?,
    text: String,
    cancelText: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title, color = titleColor ?: MaterialTheme.colorScheme.onBackground) },
            text = { Text(text) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(cancelText)
                }
            }
        )
    }
}