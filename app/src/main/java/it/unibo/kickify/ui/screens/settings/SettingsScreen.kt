package it.unibo.kickify.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import it.unibo.kickify.authentication.BiometricAuthManager
import it.unibo.kickify.data.models.Language
import it.unibo.kickify.data.models.Theme
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.AchievementDialog
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.composables.SettingsItemWithLeadingIcon
import it.unibo.kickify.ui.composables.SettingsItemWithTrailingSwitchButton
import it.unibo.kickify.ui.composables.SettingsTitleLine
import it.unibo.kickify.ui.composables.ThemeChooserRow
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    achievementsViewModel: AchievementsViewModel
) {
    val ctx = LocalContext.current
    val themeState by settingsViewModel.theme.collectAsStateWithLifecycle()
    val biometricLoginState by settingsViewModel.biometricLogin.collectAsStateWithLifecycle()
    val pushNotificationState by settingsViewModel.enabledPushNotification.collectAsStateWithLifecycle()
    val pushNotificationManager = koinInject<PushNotificationManager>()
    val languageState by settingsViewModel.appLanguage.collectAsStateWithLifecycle()
    val userid by settingsViewModel.userId.collectAsStateWithLifecycle()
    val username by settingsViewModel.userName.collectAsStateWithLifecycle()
    val userLoggedIn by settingsViewModel.isUserLoggedIn.collectAsStateWithLifecycle()

    val lastUnlockedAchievement by achievementsViewModel.lastUnlockedAchievement.collectAsStateWithLifecycle()
    val showAchievementDialog by achievementsViewModel.showAchievementDialog.collectAsStateWithLifecycle()

    LaunchedEffect(userid, username, userLoggedIn) {
        delay(1000) // wait settings to be loaded
        if(userid == "" && username == "" && !userLoggedIn){
            navController.navigate(KickifyRoute.Login){
                popUpTo(navController.graph.id){ inclusive = true }
                launchSingleTop = true
            }
        }
    }

    if(lastUnlockedAchievement != null){
        AchievementDialog(
            displayDialog = showAchievementDialog,
            achievement = lastUnlockedAchievement!!,
            onDismissRequest = {
                achievementsViewModel.dismissUnlockedAchievementDialog()
            },
            goToAchievementsPage = {
                achievementsViewModel.dismissUnlockedAchievementDialog()
                navController.navigate(KickifyRoute.Achievements)
            }
        )
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.settings_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            var showDialogLogout by rememberSaveable { mutableStateOf(false) }

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
            MessageDialog(
                showDialog = showDialogLogout,
                title = stringResource(R.string.logout),
                titleColor = null,
                text = stringResource(R.string.logout_confirmMessage),
                cancelText = stringResource(R.string.settings_cancel),
                confirmText = stringResource(R.string.logout),
                onConfirm = {
                    showDialogLogout = false
                    settingsViewModel.removeUserAccount()
                },
                onDismiss = { showDialogLogout = false}
            )

            Spacer(Modifier.height(15.dp))
            SettingsTitleLine(stringResource(R.string.settings_appSettingsTitle))

            SettingsItemWithTrailingSwitchButton(
                enabled = settingsViewModel.isStrongAuthenticationAvailable(ctx),
                textIfDisabled = stringResource(R.string.settings_biometricLoginUnavailable),
                text = stringResource(R.string.settings_enableBiometricLogin),
                checked = biometricLoginState,
                onSwitchChange = { enabled ->
                    if(enabled) {
                        if (BiometricAuthManager.canAuthenticate(ctx)) {
                            settingsViewModel.setBiometricLogin(true)
                        }
                    } else {
                        settingsViewModel.setBiometricLogin(false)
                    }
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
                    if(it == Theme.Dark){
                        achievementsViewModel.achieveAchievement(6)
                    }
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(stringResource(R.string.language))
                LanguageSelector(
                    languageCodeState = languageState,
                    onSelectedLanguageChange = { lang ->
                        settingsViewModel.setAppLanguage(
                            Language.getCodeFromLanguageString(lang)
                        )
                        if (lang == "Latinum") {
                            achievementsViewModel.achieveAchievement(7)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    languageCodeState: String,
    onSelectedLanguageChange: (String) -> Unit
) {
    val options = Language.getLanguagesStringList()
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(Language.getLanguageStringFromCode(languageCodeState))

    LaunchedEffect(languageCodeState) {
        textFieldState.setTextAndPlaceCursorAtEnd(
            Language.getLanguageStringFromCode(languageCodeState))
    }

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(fraction = 0.7f),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            state = textFieldState,
            readOnly = true,
            lineLimits = TextFieldLineLimits.SingleLine,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd(option)
                        expanded = false
                        onSelectedLanguageChange(option)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
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