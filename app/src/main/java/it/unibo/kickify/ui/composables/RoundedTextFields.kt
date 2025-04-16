package it.unibo.kickify.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import it.unibo.kickify.R
import it.unibo.kickify.ui.theme.GhostWhite
import it.unibo.kickify.ui.theme.MediumGray

@Composable
fun SearchRoundedTextField(modifier: Modifier, onSearchAction: () -> Unit){
    RoundedTextFieldGeneral(
        leadingIcon = Icons.Outlined.Search,
        singleline = true,
        keyBoardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions =  KeyboardActions(
            onSearch = {
                // when click on search button on keyboard
                onSearchAction()
            }
        ),
        trailingIcon = null,
        placeholderString = "",
        modifier = modifier
    )
}

@Composable
fun EmailRoundedTextField(modifier: Modifier, onSendAction: () -> Unit){
    RoundedTextFieldGeneral(
        leadingIcon = Icons.Outlined.Email,
        singleline = true,
        keyBoardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions =  KeyboardActions(
            onSend = {
                // when click on send button on keyboard
                onSendAction()
            }
        ),
        trailingIcon = null,
        placeholderString = stringResource(R.string.forgotpsw_templateEmail),
        modifier = modifier.padding(horizontal = 6.dp)
    )
}

@Composable
fun RoundedTextFieldGeneral(
    leadingIcon: ImageVector?,
    singleline: Boolean,
    keyBoardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    trailingIcon: ImageVector?,
    placeholderString: String,
    modifier: Modifier
) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        leadingIcon = {
            if(leadingIcon != null){
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = "",
                )
            }
        },
        trailingIcon = {
            if(trailingIcon != null){
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = ""
                )
            }
        },
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GhostWhite,
            unfocusedContainerColor = GhostWhite,
            disabledContainerColor = MediumGray
        ),
        singleLine = singleline,
        keyboardOptions = keyBoardOptions,
        keyboardActions = keyboardActions,
        placeholder = {
            Text(
                placeholderString,
                color = Color.Gray,
            )
        },
    )
}