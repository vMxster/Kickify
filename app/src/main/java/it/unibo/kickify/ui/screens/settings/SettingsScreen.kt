package it.unibo.kickify.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.SettingsItemWithLeadingIcon
import it.unibo.kickify.ui.composables.SettingsItemWithTrailingSwitchButton
import it.unibo.kickify.ui.composables.SettingsTitleLine
import it.unibo.kickify.ui.composables.ThemeChooserRow

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val themeState by settingsViewModel.theme.collectAsStateWithLifecycle()
    val locationEnabledState by settingsViewModel.enabledLocation.collectAsStateWithLifecycle()
    val biometricLoginState by settingsViewModel.biometricLogin.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.settings_title)
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

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
                icon = Icons.Outlined.Delete,
                text = stringResource(R.string.settings_deleteAccount),
                onClick = {}
            )

            Spacer(Modifier.height(15.dp))
            SettingsTitleLine(stringResource(R.string.settings_appSettingsTitle))
            if(settingsViewModel.isStrongAuthenticationAvailable(LocalContext.current)){
                SettingsItemWithTrailingSwitchButton(
                    stringResource(R.string.settings_enableBiometricLogin),
                    checked = biometricLoginState,
                    onSwitchChange = {
                        settingsViewModel.setBiometricLogin(it)
                    }
                )
            }
            SettingsItemWithTrailingSwitchButton(
                stringResource(R.string.settings_enableLocationServices),
                checked = locationEnabledState,
                onSwitchChange = {
                    settingsViewModel.setEnabledLocation(it)
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