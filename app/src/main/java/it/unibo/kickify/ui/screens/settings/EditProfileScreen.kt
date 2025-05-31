package it.unibo.kickify.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.camerax.CameraXUtils
import it.unibo.kickify.data.repositories.AppRepository
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.theme.BluePrimary
import it.unibo.kickify.ui.theme.GhostWhite
import it.unibo.kickify.ui.theme.LightGray
import it.unibo.kickify.utils.LoginRegisterUtils
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class EditProfileSections {
    USER_INFO, ADDRESS, PAYMENT_METHOD
}

@Composable
fun EditProfileScreen(
    navController: NavController,
    section: EditProfileSections,
    cameraXUtils: CameraXUtils,
    settingsViewModel: SettingsViewModel
) {
    val snackBarHostState = remember { SnackbarHostState() }

    ScreenTemplate(
        screenTitle = stringResource(R.string.editProfile_title),
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        snackBarHostState = snackBarHostState
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when(section){
                EditProfileSections.USER_INFO -> {
                    ProfileImageWithChangeButton(navController, cameraXUtils, settingsViewModel)
                    ProfileInfoChangePassword(settingsViewModel, snackBarHostState)
                }
                EditProfileSections.ADDRESS -> {
                    Text("address section")
                }
                EditProfileSections.PAYMENT_METHOD -> {
                    Text("payment method section")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageWithChangeButton(
    navController: NavController,
    cameraXUtils: CameraXUtils,
    settingsViewModel: SettingsViewModel
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val userImgState by settingsViewModel.userImg.collectAsStateWithLifecycle()
    var imageUri by remember { mutableStateOf(userImgState.toUri()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri ?: Uri.EMPTY
        settingsViewModel.setUserImg(uri.toString())
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.size(150.dp)
    ) {
        //val userBitmap = cameraXUtils.getBitmapFromUri(LocalContext.current, imageUri)

        // if found user image saved in app cache
        /*if(userBitmap != null) {
            Image(
                bitmap = userBitmap.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
            )
        } else { */ // otherwise use user profile icon
        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = "",
            modifier = Modifier.size(140.dp) .clip(CircleShape)
                .align(Alignment.TopCenter)
        )
        // }

        IconButton(
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .align(Alignment.BottomCenter),
            colors = IconButtonColors(
                containerColor = BluePrimary,
                contentColor = GhostWhite,
                disabledContainerColor = LightGray,
                disabledContentColor = GhostWhite,
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Camera Icon",
                modifier = Modifier.size(30.dp)
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            scope.launch { launcher.launch("image/*") }
                        }
                    ) {
                        Text(stringResource(R.string.profileScreen_choosePhotoFromGallery),
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate(KickifyRoute.TakeProfilePhoto) }
                    ) {
                        Text(stringResource(R.string.takePhoto),
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoChangePassword(
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState
){
    val appRepo = koinInject<AppRepository>()
    val coroutineScope = rememberCoroutineScope()
    val ctx = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val profileScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)

        var password by rememberSaveable { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }
        var pswError by remember { mutableStateOf("") }
        val passwordFocusRequester = remember { FocusRequester() }

        var confirmPassword by rememberSaveable { mutableStateOf("") }
        var confirmPasswordVisibility by remember { mutableStateOf(false) }
        var confirmPswError by remember { mutableStateOf("") }
        val confirmPswFocusRequester = remember { FocusRequester() }

        val focusManager = LocalFocusManager.current

        val userid = settingsViewModel.userId.collectAsStateWithLifecycle().value
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            modifier = profileScreenModifier,
            text = settingsViewModel.userName.collectAsStateWithLifecycle().value,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = profileScreenModifier,
            text = userid,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.password),
            modifier = profileScreenModifier
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
            modifier = profileScreenModifier.focusRequester(passwordFocusRequester)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.confirmPassword),
            modifier = profileScreenModifier
        )
        Spacer(modifier = Modifier.height(12.dp))
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
                    if(password != confirmPassword){
                        confirmPswError = ctx.getString(R.string.confirmPasswordNoMatch)
                    } else {
                        focusManager.clearFocus()
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
            modifier = profileScreenModifier.focusRequester(confirmPswFocusRequester)
        )
        Spacer(modifier = Modifier.height(15.dp))

        Button(
            modifier = profileScreenModifier,
            onClick = {
                coroutineScope.launch {
                    if(password == confirmPassword
                        && LoginRegisterUtils.isValidPassword(password)){
                        val res = appRepo.changePassword(
                            email = userid,
                            password = password
                        )
                        if(res.isSuccess){
                            snackbarHostState.showSnackbar(
                                message = ctx.getString(R.string.changedPasswordSuccessfully),
                                duration = SnackbarDuration.Long
                            )
                        } else {
                            snackbarHostState.showSnackbar(
                                message = ctx.getString(R.string.changePasswordError),
                                duration = SnackbarDuration.Long
                            )
                        }
                        password = ""
                        passwordVisibility = false
                        confirmPassword = ""
                        confirmPasswordVisibility = false
                    }
                }
            }
        ) {
            Text(stringResource(R.string.changePassword))
        }
    }
}