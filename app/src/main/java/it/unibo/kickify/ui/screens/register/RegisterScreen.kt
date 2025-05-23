package it.unibo.kickify.ui.screens.register

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.data.repositories.RemoteRepository
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.theme.MediumGray
import it.unibo.kickify.utils.LoginRegisterUtils
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun RegisterScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val ctx = LocalContext.current
    ScreenTemplate(
        screenTitle = "",
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { },
        showModalDrawer = false
    ) { contentPadding ->
        val registerScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)

        val remoteRepo = koinInject<RemoteRepository>()
        val coroutineScope = rememberCoroutineScope()

        var name by rememberSaveable { mutableStateOf("") }
        var lastname by rememberSaveable { mutableStateOf("") }
        var nameError by remember { mutableStateOf("") }
        var lastnameError by remember { mutableStateOf("") }
        val nameFocusRequester = remember { FocusRequester() }
        val lastnameFocusRequester = remember { FocusRequester() }

        var email by rememberSaveable { mutableStateOf("") }
        var emailError by remember { mutableStateOf("") }
        val emailFocusRequester = remember { FocusRequester() }

        var password by rememberSaveable { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }
        var pswError by remember { mutableStateOf("") }
        val passwordFocusRequester = remember { FocusRequester() }

        val focusManager = LocalFocusManager.current

        val registerAction: () -> Unit = {
            navController.navigate(KickifyRoute.Home) {
                popUpTo(KickifyRoute.Login) { inclusive = true }
            }
        }

        LaunchedEffect(Unit){
            nameFocusRequester.requestFocus()
        }

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(R.string.signup_title),
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = registerScreenModifier
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.signup_text),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = registerScreenModifier
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Column(
                    modifier = Modifier.fillMaxWidth(fraction = 0.5f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){
                    Text(
                        text = stringResource(R.string.signup_yourname),
                        modifier = registerScreenModifier
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = ""
                        },
                        placeholder = { Text(stringResource(R.string.signup_yourname)) },
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (LoginRegisterUtils.isValidNameLastname(name)) {
                                    lastnameFocusRequester.requestFocus()
                                } else {
                                    nameError = ctx.getString(R.string.invalidNameMessage)
                                }
                            }
                        ),
                        isError = nameError != "",
                        supportingText = {
                            if (nameError != "") {
                                Text(text = nameError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 24.dp)
                            .padding(end = 8.dp)
                            .focusRequester(nameFocusRequester)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ){
                    Text(
                        text = stringResource(R.string.signup_yourLastName),
                        modifier = registerScreenModifier
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = lastname,
                        onValueChange = {
                            lastname = it
                            lastnameError = ""
                        },
                        placeholder = { Text(stringResource(R.string.signup_yourLastName)) },
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (LoginRegisterUtils.isValidNameLastname(lastname)) {
                                    emailFocusRequester.requestFocus()
                                } else {
                                    lastnameError = ctx.getString(R.string.invalidLastnameMessage)
                                }
                            }
                        ),
                        isError = lastnameError != "",
                        supportingText = {
                            if (lastnameError != "") {
                                Text(text = lastnameError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 8.dp)
                            .padding(end = 24.dp).focusRequester(lastnameFocusRequester)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.emailAddress),
                modifier = registerScreenModifier
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
                modifier = registerScreenModifier.focusRequester(emailFocusRequester)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.password),
                modifier = registerScreenModifier
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
                        if (LoginRegisterUtils.isValidPassword(password)) {
                            focusManager.clearFocus()
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
                modifier = registerScreenModifier.focusRequester(passwordFocusRequester)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        if(LoginRegisterUtils.isValidEmail(email)
                            && LoginRegisterUtils.isValidPassword(password)
                            && LoginRegisterUtils.isValidNameLastname(name)
                            && LoginRegisterUtils.isValidNameLastname(lastname)) {
                            val registerRes = remoteRepo.register(
                                email = email, firstName = name,
                                lastName = lastname, password = password,
                                newsletter = false, phone = null
                            )
                            if (registerRes.isSuccess) {
                                Toast.makeText(ctx, "Register successful", Toast.LENGTH_LONG).show()
                                settingsViewModel.setUserAccount(
                                    userid = email, username = "$name $lastname"
                                )
                                Log.i("LOGIN", "userid: $email - username: '$name $lastname'")
                                registerAction()
                            } else {
                                Toast.makeText(ctx, "Register ERROR", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(ctx, "Check all fields are valid", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = registerScreenModifier,
            ) {
                Text(
                    text = stringResource(R.string.signup_button),
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

            LoginRegisterMethodDividerRow()
            Button(
                onClick = { /* google oauth login */ },
                modifier = registerScreenModifier,
                colors = ButtonDefaults.buttonColors(containerColor = MediumGray)
            ) {
                Image(
                    painter = painterResource(R.drawable.google_icon),
                    contentDescription = "Login with Google"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.signin_signup_continueGoogle),
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

            TextButton(
                onClick = {
                    navController.navigate(KickifyRoute.Login) {
                        popUpTo(KickifyRoute.Login) { inclusive = true }
                    }
                },
                modifier = registerScreenModifier,
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)){
                            append(stringResource(R.string.signup_alreadyPartOfCrew) + " ")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                            append(stringResource(R.string.signin_button))
                        }
                    },
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    }
}

@Composable
fun LoginRegisterMethodDividerRow(){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp).padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = stringResource(R.string.or),
            color = MaterialTheme.colorScheme.inverseSurface,
        )
    }
}