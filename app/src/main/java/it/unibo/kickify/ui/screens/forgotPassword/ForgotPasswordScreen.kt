package it.unibo.kickify.ui.screens.forgotPassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.utils.LoginRegisterUtils

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    forgotPasswordOTPViewModel: ForgotPasswordOTPViewModel
) {
    val ctx = LocalContext.current

    val isLoading by forgotPasswordOTPViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by forgotPasswordOTPViewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by forgotPasswordOTPViewModel.successMessage.collectAsStateWithLifecycle()

    val goToOtpScreen: () -> Unit = {
        navController.navigate(KickifyRoute.OTPScreen)
    }

    LaunchedEffect(successMessage) {
        if(successMessage == "Email valida."){
            goToOtpScreen()
        }
    }

    ScreenTemplate(
        screenTitle = stringResource(R.string.signin_forgotPassword),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false,
        showLoadingOverlay = isLoading
    ) {
        val forgotScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp)

        var email by rememberSaveable { mutableStateOf("") }
        var emailError by remember { mutableStateOf("") }
        val emailFocusRequester = remember { FocusRequester() }

        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier.fillMaxSize()
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
                text = stringResource(R.string.forgotpsw_text),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = forgotScreenModifier
            )
            Text(
                text = stringResource(R.string.emailAddress),
                modifier = forgotScreenModifier.padding(top = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                placeholder = { Text(stringResource(R.string.emailAddress)) },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        if (LoginRegisterUtils.isValidEmail(email)) {
                            focusManager.clearFocus()
                        } else {
                            emailError = ctx.getString(R.string.invalidEmailMessage)
                        }
                    }
                ),
                isError = emailError != "",
                supportingText = {
                    if (emailError != "") {
                        Text(text = emailError, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = forgotScreenModifier.focusRequester(emailFocusRequester)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(LoginRegisterUtils.isValidEmail(email)
                        && forgotPasswordOTPViewModel.isValidEmail(email)){
                        goToOtpScreen()
                    } else {
                        emailError = ctx.getString(R.string.invalidEmailMessage)
                    }
                },
                modifier = forgotScreenModifier
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                )
            }
            errorMessage?.let { Text(it, modifier = forgotScreenModifier) }
            successMessage?.let { Text(it, modifier = forgotScreenModifier) }
        }
    }
}
