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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import it.unibo.kickify.R
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.register.LoginRegisterMethodDividerRow
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.utils.LoginRegisterUtils
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    loginViewModel: LoginViewModel,
    achievementsViewModel: AchievementsViewModel,
    onLoginSuccess: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val focusManager = LocalFocusManager.current

    val isLoading by loginViewModel.isLoading.collectAsStateWithLifecycle()
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val message by loginViewModel.message.collectAsStateWithLifecycle()
    val loggedInUser by loginViewModel.loggedInUser.collectAsStateWithLifecycle()

    var email by rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    val emailFocusRequester = remember { FocusRequester() }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val passwordFocusRequester = remember { FocusRequester() }

    val credentialManager = remember {
        val manager = CredentialManager.create(ctx)
        manager
    }

    // Funzione per gestire l'accesso con Google
    fun signInWithGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("897714702292-ngm2eni5m4c06j32u3uu68ggifm10oj6.apps.googleusercontent.com")
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val response: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = ctx
                )

                // Estrattore per ottenere il token indipendentemente dal tipo di credenziale
                val idToken = when (val credential = response.credential) {
                    is GoogleIdTokenCredential -> {
                        credential.idToken
                    }
                    else -> {
                        try {
                            val credentialBundle = credential.data
                            credentialBundle.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN")
                                ?: throw Exception("Token non trovato nel bundle")
                        } catch (e: Exception) {
                            val errorMsg = "Impossibile estrarre il token: ${e.message}"
                            snackBarHostState.showSnackbar(
                                message = errorMsg,
                                actionLabel = ctx.getString(R.string.ok),
                                duration = SnackbarDuration.Long)
                            null
                        }
                    }
                }

                // Continua solo se abbiamo ottenuto un token
                if (idToken != null) {
                    loginViewModel.loginWithGoogle(idToken)
                } else {
                    snackBarHostState.showSnackbar(
                        message = "Token non disponibile",
                        actionLabel = ctx.getString(R.string.ok),
                        duration = SnackbarDuration.Long)
                }
            } catch (e: GetCredentialException) {
                when (e) {
                    is GetCredentialCancellationException ->
                        snackBarHostState.showSnackbar(
                            message = "Autenticazione annullata",
                            actionLabel = ctx.getString(R.string.ok),
                            duration = SnackbarDuration.Long
                        )
                    else -> snackBarHostState.showSnackbar(
                        message = "Errore durante l'autenticazione: ${e.message}",
                        actionLabel = ctx.getString(R.string.ok),
                        duration = SnackbarDuration.Long
                    )
                }
            } catch (e: Exception) {
                snackBarHostState.showSnackbar(
                    message = "Errore imprevisto: ${e.message}",
                    actionLabel = ctx.getString(R.string.ok),
                    duration = SnackbarDuration.Long)
            }
        }
    }

    // set onboarding completed
    settingsViewModel.setOnboardingComplete(true)

    // if logged in successfully, execute login action
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && loggedInUser != null) {
            settingsViewModel.setUserAccount(
                userid = loggedInUser?.email ?: "",
                username = "${loggedInUser?.name} ${loggedInUser?.surname}"
            )
            onLoginSuccess()
            loginViewModel.clearData()
        }
    }

    // show message if present
    LaunchedEffect(message) {
        message?.let { message ->
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long,
                actionLabel = ctx.getString(R.string.ok))
            loginViewModel.dismissMessage()
        }
    }

    ScreenTemplate(
        screenTitle = "",
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false,
        snackBarHostState = snackBarHostState,
        showLoadingOverlay = isLoading,
        achievementsViewModel = achievementsViewModel
    ) {
        val loginScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)

        Column(
            modifier = Modifier.fillMaxSize()
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
                    passwordError = ""
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
                        } else if(password.isEmpty()){
                            passwordError = ctx.getString(R.string.passwordEmptyError)
                        }
                    }
                ),
                isError = passwordError != "",
                supportingText = {
                    if (passwordError != "") {
                        Text(text = passwordError, color = MaterialTheme.colorScheme.error)
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
                    if(LoginRegisterUtils.isValidEmail(email)
                        && password.isNotEmpty()){
                        loginViewModel.login(email, password)
                    } else {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = ctx.getString(R.string.signin_errorsInEmailOrPassword),
                                actionLabel = ctx.getString(R.string.ok),
                                duration = SnackbarDuration.Long)
                        }
                    }
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

            LoginRegisterMethodDividerRow()
            TextButton(
                onClick = { signInWithGoogle() },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.google_icon),
                    contentDescription = stringResource(R.string.loginGoogle)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.signin_signup_continueGoogle),
                    color = MaterialTheme.colorScheme.onBackground
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
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)){
                            append(stringResource(R.string.signin_newToKickify) + " ")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                            append(stringResource(R.string.signin_joinForFree))
                        }
                    },
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    }
}