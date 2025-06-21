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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.utils.LoginRegisterUtils
import kotlinx.coroutines.delay

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    forgotPasswordOTPViewModel: ForgotPasswordOTPViewModel,
    achievementsViewModel: AchievementsViewModel
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current

    val isLoading by forgotPasswordOTPViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by forgotPasswordOTPViewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by forgotPasswordOTPViewModel.successMessage.collectAsStateWithLifecycle()

    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var pswError by remember { mutableStateOf("") }
    val passwordFocusRequester = remember { FocusRequester() }

    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var confirmPswError by remember { mutableStateOf("") }
    val confirmPswFocusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    val gotoLogin: () -> Unit = {
        navController.navigate(KickifyRoute.Login) {
            popUpTo<KickifyRoute.ResetPasswordScreen> { inclusive = true }
            launchSingleTop = true
        }
    }

    LaunchedEffect(Unit) {
        delay(5000)
        forgotPasswordOTPViewModel.dismissMessages()
    }

    LaunchedEffect(successMessage) {
        if(successMessage == "Password cambiata con successo. Effettua il login."){
            snackBarHostState.showSnackbar(
                message = ctx.getString(R.string.changedPasswordSuccessfully) + "\n" +
                        ctx.getString(R.string.nowLoginWithNewPassword),
                duration = SnackbarDuration.Long,
                actionLabel = ctx.getString(R.string.ok)
            )
            gotoLogin()
        }
    }

    ScreenTemplate(
        screenTitle = "",
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false,
        showLoadingOverlay = isLoading,
        snackBarHostState = snackBarHostState,
        achievementsViewModel = achievementsViewModel
    ) {
        val resetScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)

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
                modifier = resetScreenModifier
            )
            Text(
                text = stringResource(R.string.resetpsw_setNewPassword),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = resetScreenModifier
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.password),
                modifier = resetScreenModifier
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    pswError = ""
                },
                placeholder = { Text(stringResource(R.string.password))},
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        if (LoginRegisterUtils.isValidPassword(password)) {
                            confirmPswFocusRequester.requestFocus()
                        } else {
                            pswError = ctx.getString(R.string.invalidPswMessage)
                        }
                    }
                ),
                isError = pswError != "",
                supportingText = {
                    if (pswError != "") {
                        Text(text = pswError, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    val image = if (passwordVisibility) Icons.Outlined.Visibility
                    else Icons.Outlined.VisibilityOff

                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = image,
                            contentDescription = stringResource(R.string.showHidePsw))
                    }
                },
                modifier = resetScreenModifier.focusRequester(passwordFocusRequester)
            )

            Text(
                text = stringResource(R.string.confirmPassword),
                modifier = resetScreenModifier
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPswError = ""
                },
                placeholder = { Text(stringResource(R.string.confirmPassword))},
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if(!LoginRegisterUtils.isValidPassword(confirmPassword)){
                            confirmPswError = ctx.getString(R.string.invalidPswMessage)
                        }
                        if(password == confirmPassword){
                            focusManager.clearFocus()
                        } else {
                            confirmPswError = ctx.getString(R.string.confirmPasswordNoMatch)
                        }
                    }
                ),
                isError = confirmPswError != "",
                supportingText = {
                    if (confirmPswError != "") {
                        Text(text = confirmPswError, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    val image = if (confirmPasswordVisibility) Icons.Outlined.Visibility
                    else Icons.Outlined.VisibilityOff

                    IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                        Icon(imageVector = image,
                            contentDescription = stringResource(R.string.showHidePsw))
                    }
                },
                modifier = resetScreenModifier.focusRequester(confirmPswFocusRequester)
            )

            Button(
                onClick = {
                    if(password != confirmPassword) {
                        confirmPswError = ctx.getString(R.string.confirmPasswordNoMatch)

                    } else if(!LoginRegisterUtils.isValidPassword(password)
                        || !LoginRegisterUtils.isValidPassword(confirmPassword)){
                        confirmPswError = ctx.getString(R.string.invalidPswMessage)

                    } else {
                        forgotPasswordOTPViewModel.resetPassword(password)
                    }
                },
                modifier = resetScreenModifier,
            ) {
                Text(
                    text = stringResource(R.string.continue_button),
                )
            }

            errorMessage?.let {
                Text(it, modifier = resetScreenModifier, textAlign = TextAlign.Center)
            }
            /*successMessage?.let {
                Text(it, modifier = resetScreenModifier, textAlign = TextAlign.Center)
            }*/
        }
    }
}
