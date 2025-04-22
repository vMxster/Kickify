package it.unibo.kickify.ui.composables

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
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
    onChangeCheckedAction: () -> Unit
) {
    var checked by remember { mutableStateOf(isChecked) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
            onChangeCheckedAction()
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