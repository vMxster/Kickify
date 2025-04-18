package it.unibo.kickify.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SettingsTitleLine(title: String){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 6.dp)
            .height(30.dp)
    ){
        Text(title,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun SettingsItemWithLeadingIcon(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(icon,
            contentDescription = "",
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start
        )
        Icon(
            Icons.Outlined.ChevronRight,
            modifier = Modifier.clickable(onClick = onClick)
                .size(30.dp),
            contentDescription = "",
        )
    }
}

@Composable
fun SettingsItemWithTrailingSwitchButton(
    text: String,
    checked: Boolean,
    onSwitchChange: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
        SwitchButton(
            isChecked = checked,
            onChangeCheckedAction = onSwitchChange,
        )
    }
}