package it.unibo.kickify.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.ScreenTemplate

@Composable
fun OTPScreen(
    navController: NavController
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ScreenTemplate(
        screenTitle = "",
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false
    ) { contentPadding ->
        val forgotScreenModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp)

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding)
                .padding(top = 20.dp)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.forgotpsw_title),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = forgotScreenModifier
            )
            Text(
                text = stringResource(R.string.otpScreen_text),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = forgotScreenModifier
            )
            Text(
                text = stringResource(R.string.otpScreen_otp),
                modifier = forgotScreenModifier.padding(top = 16.dp)
            )

            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
                otpValues.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            if(it.length <= 1){
                                otpValues[index] = it
                                if(it.isNotEmpty() && index <= 5){
                                    focusManager.moveFocus(FocusDirection.Next)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = if (index == 5) ImeAction.Done else ImeAction.Next
                        ),
                        modifier =
                            if(index == 0) Modifier.width(50.dp)
                                .focusRequester(focusRequester)
                            else Modifier.width(50.dp),
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                    )
                }
            }

            Button(
                onClick = {
                    navController.navigate(KickifyRoute.Login) {
                        popUpTo(KickifyRoute.ForgotPassword) { inclusive = true }
                    }
                },
                modifier = forgotScreenModifier,
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                )
            }
        }
    }
}
