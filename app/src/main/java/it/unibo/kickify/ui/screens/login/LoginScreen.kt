package it.unibo.kickify.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.theme.MediumGray
import it.unibo.kickify.utils.LoginRegisterUtils

@Composable
fun LoginScreen(navController: NavController) {
    val ctx = LocalContext.current

    var email by rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    val emailFocusRequester = remember { FocusRequester() }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var pswError by remember { mutableStateOf("") }
    val passwordFocusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    val loginAction: () -> Unit = {
        navController.navigate(KickifyRoute.Home) {
            popUpTo(KickifyRoute.Login) { inclusive = true }
        }
    }

    ScreenTemplate(
        screenTitle = "",
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false
    ) { contentPadding ->
        val loginScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.signin_Title),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = loginScreenModifier
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.signin_Text),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = loginScreenModifier
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.emailAddress),
                modifier =loginScreenModifier
            )
            Spacer(modifier = Modifier.height(12.dp))
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
                            passwordFocusRequester.requestFocus()
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
                modifier = loginScreenModifier.focusRequester(emailFocusRequester)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.password),
                modifier =loginScreenModifier
            )
            Spacer(modifier = Modifier.height(12.dp))
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (password.isNotEmpty()) {
                            focusManager.clearFocus()
                            loginAction()

                        } else {
                            pswError = ctx.getString(R.string.emptyPswMessage)
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
                modifier = loginScreenModifier.focusRequester(passwordFocusRequester)
            )

            TextButton(
                onClick = { navController.navigate(KickifyRoute.ForgotPassword) },
                modifier = Modifier.align(Alignment.End)
                    .padding(end = 28.dp)
            ) {
                Text(
                    text = stringResource(R.string.signin_forgotPassword),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    //ctx.startActivity(Intent(ctx, AuthActivity::class.java))
                    loginAction()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                Text(
                    text = stringResource(R.string.signin_button),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = { /* google oauth */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MediumGray)
            ) {
                Image(
                    painter = painterResource(R.drawable.google_icon),
                    contentDescription = stringResource(R.string.loginGoogle)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.signin_signup_continueGoogle),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            TextButton(
                onClick = { navController.navigate(KickifyRoute.Register) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.signin_joinForFree),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    }
}