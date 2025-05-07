package it.unibo.kickify.ui.composables

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import it.unibo.kickify.ui.theme.BluePrimary
import it.unibo.kickify.ui.theme.GhostWhite

@Composable
fun SwitchButton(
    isChecked: Boolean,
    onChangeCheckedAction: (Boolean) -> Unit
) {
    var switchState by remember { mutableStateOf(isChecked) }

    LaunchedEffect(isChecked) {
        switchState = isChecked
    }

    Switch(
        checked = switchState,
        onCheckedChange = { newValue ->
            switchState = newValue
            onChangeCheckedAction(newValue)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = GhostWhite,
            uncheckedThumbColor = GhostWhite,
            checkedTrackColor = BluePrimary,
            checkedBorderColor = BluePrimary,
            uncheckedBorderColor = Color.Unspecified
        )
    )
}