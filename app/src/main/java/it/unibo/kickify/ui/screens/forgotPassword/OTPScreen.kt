package it.unibo.kickify.ui.screens.forgotPassword

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import kotlinx.coroutines.delay

@Composable
fun OTPScreen(
    navController: NavController,
    forgotPasswordOTPViewModel: ForgotPasswordOTPViewModel,
    achievementsViewModel: AchievementsViewModel
) {
    val otpLength = 6
    val focusManager = LocalFocusManager.current
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }

    val ctx = LocalContext.current

    val isLoading by forgotPasswordOTPViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by forgotPasswordOTPViewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by forgotPasswordOTPViewModel.successMessage.collectAsStateWithLifecycle()

    var showOTPSupportText by remember { mutableStateOf(false) }

    val goToResetPswScreen: () -> Unit = {
        navController.navigate(KickifyRoute.ResetPasswordScreen) {
            popUpTo(KickifyRoute.OTPScreen) { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
        delay(5000)
        forgotPasswordOTPViewModel.dismissMessages()
    }

    LaunchedEffect(successMessage) {
        if (successMessage == "OTP verificato con successo.") {
            goToResetPswScreen()
        }
    }

    ScreenTemplate(
        screenTitle = "",
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false,
        showLoadingOverlay = isLoading,
        achievementsViewModel = achievementsViewModel
    ) {
        val otpScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp)

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
                modifier = otpScreenModifier
            )
            Text(
                text = stringResource(R.string.otpScreen_text),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = otpScreenModifier
            )
            Text(
                text = stringResource(R.string.otpScreen_otp),
                modifier = otpScreenModifier.padding(top = 16.dp)
            )
            Row(
                modifier = otpScreenModifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                otpValues.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newValue ->
                            showOTPSupportText = false
                            if (newValue.length <= 1) {
                                otpValues[index] = newValue

                                if (newValue.isNotEmpty()) {
                                    if (index < otpLength - 1) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else {
                                        focusManager.clearFocus()
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (index == 5) ImeAction.Done else ImeAction.Next
                        ),
                        modifier = Modifier.width(50.dp)
                            .focusRequester(focusRequesters[index])
                            .onKeyEvent { event ->
                                if (event.key == Key.Backspace && otpValues[index].isEmpty()) {
                                    if (index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                    true // consume event
                                } else {
                                    false // don't consume event
                                }
                            },
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                    )
                }
            }

            Button(
                onClick = {
                    if(otpValues.all { it.isNotEmpty() }) {
                        val otp = otpValues.joinToString(separator = "")
                        forgotPasswordOTPViewModel.verifyOtp(otp)
                    } else {
                        showOTPSupportText = true
                    }
                },
                modifier = otpScreenModifier,
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                )
            }

            errorMessage?.let {
                Text(it, modifier = otpScreenModifier, textAlign = TextAlign.Center)
            }
            successMessage?.let {
                Text(it, modifier = otpScreenModifier, textAlign = TextAlign.Center)
            }

            if(showOTPSupportText) {
                Text(
                    text = stringResource(R.string.otp_missing_digits),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = otpScreenModifier
                )
            }
        }
    }
}
