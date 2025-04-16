package it.unibo.kickify.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import it.unibo.kickify.R

@Composable
fun SearchRoundedTextField(modifier: Modifier, onSearchAction: () -> Unit){
    RoundedTextFieldGeneralWithLeadingIcon(
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
        placeholderString = "",
        passwordTransformation = false,
        modifier = modifier.padding(horizontal = 6.dp)
    )
}

@Composable
fun UsernameRoundedTextField(modifier: Modifier){
    RoundedTextFieldGeneral(
        singleline = true,
        keyBoardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions =  KeyboardActions(),
        placeholderString = stringResource(R.string.signup_yourname),
        passwordTransformation = false,
        modifier = modifier.padding(horizontal = 6.dp)
    )
}

@Composable
fun PasswordRoundedTextField(modifier: Modifier, onSendAction: () -> Unit){
    RoundedTextFieldGeneral(
        singleline = true,
        keyBoardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions =  KeyboardActions(
            onSend = {
                // when click on send button on keyboard
            }
        ),
        placeholderString = stringResource(R.string.password),
        modifier = modifier.padding(horizontal = 6.dp),
        passwordTransformation = true
    )
}

@Composable
fun EmailRoundedTextField(modifier: Modifier, placeholderString: String, onSendAction: () -> Unit){
    RoundedTextFieldGeneral(
        singleline = true,
        keyBoardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions =  KeyboardActions(
            onSend = {
                // when click on send button on keyboard
                onSendAction()
            }
        ),
        placeholderString = placeholderString,
        passwordTransformation = false,
        modifier = modifier.padding(horizontal = 6.dp)
    )
}

@Composable
fun RoundedTextFieldGeneral(
    singleline: Boolean,
    keyBoardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    placeholderString: String,
    passwordTransformation: Boolean,
    modifier: Modifier
) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleline,
        keyboardOptions = keyBoardOptions,
        keyboardActions = keyboardActions,
        placeholder = {
            Text(
                placeholderString,
                color = Color.Gray,
            )
        },
        visualTransformation = when(passwordTransformation) {
            true -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        }
    )
}

@Composable
fun RoundedTextFieldGeneralWithLeadingIcon(
    leadingIcon: ImageVector,
    singleline: Boolean,
    keyBoardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    placeholderString: String,
    passwordTransformation: Boolean,
    modifier: Modifier
) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleline,
        keyboardOptions = keyBoardOptions,
        keyboardActions = keyboardActions,
        placeholder = {
            Text(
                placeholderString,
                color = Color.Gray,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "",
            )
        },
        visualTransformation = when(passwordTransformation) {
            true -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        }
    )
}