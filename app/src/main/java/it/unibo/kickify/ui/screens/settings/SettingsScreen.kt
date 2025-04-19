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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.SettingsItemWithLeadingIcon
import it.unibo.kickify.ui.composables.SettingsItemWithTrailingSwitchButton
import it.unibo.kickify.ui.composables.SettingsTitleLine

@Composable
fun SettingsScreen(
    navController: NavController = NavController(LocalContext.current)
    //state: ThemeState,
    //onThemeSelected: (Theme) -> Unit*/
) {
    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.settings_title)
            )
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
            SettingsItemWithTrailingSwitchButton(
                stringResource(R.string.settings_enableFaceIDtoLogin),
                checked = false,
                onSwitchChange = { }
            )
            SettingsItemWithTrailingSwitchButton(
                stringResource(R.string.settings_enableFingerprintToLogin),
                checked = false,
                onSwitchChange = { }
            )
            SettingsItemWithTrailingSwitchButton(
                stringResource(R.string.settings_enableLocationServices),
                checked = true,
                onSwitchChange = { }
            )
            SettingsItemWithTrailingSwitchButton(
                stringResource(R.string.settings_darkTheme),
                checked = true,
                onSwitchChange = { }
            )
        }
    }
}