package it.unibo.kickify.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.unibo.kickify.R
import it.unibo.kickify.data.models.Theme
import it.unibo.kickify.ui.composables.SwitchButton
import it.unibo.kickify.ui.theme.KickifyTheme

@Preview
@Composable
fun SettingsScreen(
    //state: ThemeState,
    //onThemeSelected: (Theme) -> Unit*/
) {
    KickifyTheme {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.selectableGroup())
        {
            Text(text = stringResource(R.string.settings_chooseTheme),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 16.dp)
            )
            Theme.entries.forEach { theme ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            /*selected = (theme == state.theme),
                            onClick = { onThemeSelected(theme) },*/
                            selected = false,
                            onClick = {},
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false, //(theme == state.theme),
                        onClick = null
                    )
                    Text(
                        text = getLocalizedThemeString(theme),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ){
                Text(text = stringResource(R.string.settings_enableLoginWithFingerPrint),
                    color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(26.dp))
                SwitchButton(checked = false)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun getLocalizedThemeString(theme: Theme) : String {
    return when(theme) {
        Theme.Light -> stringResource(R.string.theme_light)
        Theme.Dark -> stringResource(R.string.theme_dark)
        else -> stringResource(R.string.theme_system)
    }
}