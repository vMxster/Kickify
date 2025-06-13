package it.unibo.kickify.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.ScreenTemplate
import it.unibo.kickify.ui.screens.achievements.AchievementsViewModel
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.theme.BluePrimary
import it.unibo.kickify.ui.theme.GhostWhite
import it.unibo.kickify.ui.theme.LightGray
import it.unibo.kickify.utils.LoginRegisterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

enum class EditProfileSections {
    USER_INFO, ADDRESS, PAYMENT_METHOD
}

@Composable
fun EditProfileScreen(
    navController: NavController,
    section: EditProfileSections,
    cameraXUtils: CameraXUtils,
    settingsViewModel: SettingsViewModel,
    achievementsViewModel: AchievementsViewModel
) {
    val ctx = LocalContext.current

    val snackBarHostState = remember { SnackbarHostState() }
    val isLoadingSettings by settingsViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessageSettings by settingsViewModel.errorMessage.collectAsStateWithLifecycle()
    val userEmail by settingsViewModel.userId.collectAsStateWithLifecycle()

    val profileViewModel = koinViewModel<ProfileViewModel>()
    val isLoadingProfile by profileViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessageProfile by profileViewModel.errorMessage.collectAsStateWithLifecycle()
    val addressModified by profileViewModel.addressListModified.collectAsStateWithLifecycle()
    val modifiedPassword by profileViewModel.passwordModified.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(modifiedPassword) {
        if(modifiedPassword && errorMessageProfile == null){
            snackBarHostState.showSnackbar(
                message = ctx.getString(R.string.changedPasswordSuccessfully),
                duration = SnackbarDuration.Long
            )
        }
        if(modifiedPassword && errorMessageProfile != null){
            snackBarHostState.showSnackbar(
                message = ctx.getString(R.string.changePasswordError),
                duration = SnackbarDuration.Long
            )
        }
        profileViewModel.resetChangedPassword()
        profileViewModel.dismissError()
    }

    LaunchedEffect(addressModified) {
        if(addressModified){
            profileViewModel.resetModifiedAddress()
            navController.popBackStack()
        }
    }

    LaunchedEffect(errorMessageProfile) {
        errorMessageProfile?.let {
            snackBarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
        }
    }

    LaunchedEffect(errorMessageSettings) {
        errorMessageSettings?.let {
            snackBarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
        }
    }

    ScreenTemplate(
        screenTitle = when(section) {
            EditProfileSections.USER_INFO -> stringResource(R.string.editProfile_title)
            EditProfileSections.ADDRESS -> stringResource(R.string.addNewAddress)
            EditProfileSections.PAYMENT_METHOD -> stringResource(R.string.paymentMethod)
        },
        navController = navController,
        showTopAppBar = true,
        bottomAppBarContent = { BottomBar(navController) },
        showModalDrawer = true,
        snackBarHostState = snackBarHostState,
        showLoadingOverlay = isLoadingSettings || isLoadingProfile,
        achievementsViewModel = achievementsViewModel
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when(section){
                EditProfileSections.USER_INFO -> {
                    ProfileImageWithChangeButton(navController, cameraXUtils, settingsViewModel, snackBarHostState)
                    ProfileInfoChangePassword(settingsViewModel, profileViewModel)
                }

                EditProfileSections.ADDRESS -> {
                    EditAddressSection(snackBarHostState, profileViewModel, userEmail, coroutineScope)
                }

                EditProfileSections.PAYMENT_METHOD -> {
                    Text("payment method section")
                }
            }
        }
    }
}

@Composable
fun EditAddressSection(
    snackBarHostState: SnackbarHostState,
    profileViewModel: ProfileViewModel,
    userEmail: String,
    coroutineScope: CoroutineScope
){
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val profileScreenModifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)

        val focusManager = LocalFocusManager.current

        var streetName by rememberSaveable { mutableStateOf("") }
        val streetNameFocusRequester = remember { FocusRequester() }

        var number by rememberSaveable { mutableStateOf("") }
        val numberFocusRequester = remember { FocusRequester() }

        var city by rememberSaveable { mutableStateOf("") }
        val cityFocusRequester = remember { FocusRequester() }

        var cap by rememberSaveable { mutableStateOf("") }
        val capFocusRequester = remember { FocusRequester() }

        var province by rememberSaveable { mutableStateOf("") }
        val provinceFocusRequester = remember { FocusRequester() }

        var nation by rememberSaveable { mutableStateOf("") }
        val nationFocusRequester = remember { FocusRequester() }

        var defaultAddress by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            streetNameFocusRequester.requestFocus()
        }

        Text(text = stringResource(R.string.streetNameAddress), modifier = profileScreenModifier)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = streetName,
            onValueChange = { streetName = it },
            placeholder = { Text(stringResource(R.string.streetNameAddress)) },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { numberFocusRequester.requestFocus() }
            ),
            modifier = profileScreenModifier.focusRequester(streetNameFocusRequester)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = stringResource(R.string.streetNumberAddress), modifier = profileScreenModifier)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            placeholder = { Text(stringResource(R.string.streetNumberAddress)) },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { cityFocusRequester.requestFocus() }
            ),
            modifier = profileScreenModifier.focusRequester(numberFocusRequester)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = stringResource(R.string.cityAddress), modifier = profileScreenModifier)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            placeholder = { Text(stringResource(R.string.cityAddress)) },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { capFocusRequester.requestFocus() }
            ),
            modifier = profileScreenModifier.focusRequester(cityFocusRequester)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = stringResource(R.string.capAddress), modifier = profileScreenModifier)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = cap,
            onValueChange = { cap = it },
            placeholder = { Text(stringResource(R.string.capAddress)) },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { provinceFocusRequester.requestFocus() }
            ),
            modifier = profileScreenModifier.focusRequester(capFocusRequester)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = stringResource(R.string.provinceAddress), modifier = profileScreenModifier)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = province,
            onValueChange = { province = it },
            placeholder = { Text(stringResource(R.string.provinceAddress)) },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { nationFocusRequester.requestFocus() }
            ),
            modifier = profileScreenModifier.focusRequester(provinceFocusRequester)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = stringResource(R.string.nationAddress), modifier = profileScreenModifier)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = nation,
            onValueChange = { nation = it },
            placeholder = { Text(stringResource(R.string.nationAddress)) },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = profileScreenModifier.focusRequester(nationFocusRequester)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = profileScreenModifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(stringResource(R.string.setDefaultAddress))
            Switch(checked = defaultAddress,
                onCheckedChange = { defaultAddress = it }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val areFieldsOk = listOf(streetName, number, cap, city, province, nation)
                    .all { it.isNotBlank() }
                if(areFieldsOk){
                    profileViewModel.addUserAddress(
                        userEmail, streetName, number, cap, city,
                        province, nation, defaultAddress
                    )
                } else {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Fill in all fields",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            },
            modifier = profileScreenModifier
        ){
            Text(stringResource(R.string.addNewAddress))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageWithChangeButton(
    navController: NavController,
    cameraXUtils: CameraXUtils,
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState
) {
    val ctx = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val errorMessage by settingsViewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by settingsViewModel.successMessage.collectAsStateWithLifecycle()
    val userImg by settingsViewModel.userImg.collectAsStateWithLifecycle()
    var imageUri by remember { mutableStateOf(userImg.toUri()) }
    val userEmail by settingsViewModel.userId.collectAsStateWithLifecycle()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = ctx.getString(R.string.ok),
                duration = SnackbarDuration.Long)
        }
        settingsViewModel.dismissMessage()
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = ctx.getString(R.string.ok),
                duration = SnackbarDuration.Long)
        }
        settingsViewModel.dismissMessage()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imageUri = uri ?: Uri.EMPTY
        val bitmapImg = cameraXUtils.getBitmapFromSelector(imageUri)
        val mimeType = cameraXUtils.getImageMimeType(imageUri)

        if(bitmapImg != null){
            val byteArray = cameraXUtils.bitmapToByteArray(bitmapImg)
            if(byteArray != null) {
                settingsViewModel.setUserImg(userEmail, byteArray, mimeType)
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = ctx.getString(R.string.choosePhotoFromGalleryError),
                    actionLabel = ctx.getString(R.string.ok),
                    duration = SnackbarDuration.Long)
            }
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.size(150.dp)
    ) {
        UserProfileIcon(userImg,
            userImgModifier = Modifier.size(120.dp)
                .clip(RoundedCornerShape(10.dp))
                .align(Alignment.Center),
            accountIconModifier = Modifier.size(120.dp).clip(CircleShape)
                .align(Alignment.Center)
        )

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
                            showBottomSheet = false
                            navController.navigate(KickifyRoute.TakeProfilePhoto)
                        }
                    ) {
                        Text(stringResource(R.string.takePhoto),
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            scope.launch {
                                showBottomSheet = false
                                launcher.launch(PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        }
                    ) {
                        Text(stringResource(R.string.profileScreen_choosePhotoFromGallery),
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
    profileViewModel: ProfileViewModel
){
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
                        profileViewModel.changePassword(
                            email = userid,
                            password = password
                        )
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

@Composable
fun AddressContainer(
    index: Int,
    address: String,
    number: String,
    city: String,
    province: String,
    postCode: String,
    country: String,
    defaultAddress: Boolean,
    deleteAction: () -> Unit
){
    val txtModifier = Modifier.fillMaxWidth().padding(start = 10.dp).padding(vertical = 2.dp)
    Row(
        modifier = txtModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(modifier = Modifier.fillMaxWidth(fraction = 0.7f).padding(vertical = 8.dp)) {
            val addrDescr = if(defaultAddress) stringResource(R.string.address) + " ${index+1} - " + stringResource(R.string.default_string)
            else stringResource(R.string.address) + " ${index+1}"
            Text(text = addrDescr, modifier = txtModifier,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "$address, $number", textAlign = TextAlign.Start, modifier = txtModifier)
            Text(text = "$city, $postCode, $province", textAlign = TextAlign.Start, modifier = txtModifier)
            Text(text = country, textAlign = TextAlign.Start, modifier = txtModifier)
        }
        IconButton(
            onClick = { deleteAction() },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(Icons.Outlined.Delete, contentDescription = "")
        }
    }
}
