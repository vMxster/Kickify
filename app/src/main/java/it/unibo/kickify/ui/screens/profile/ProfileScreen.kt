package it.unibo.kickify.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import it.unibo.kickify.R
import it.unibo.kickify.camerax.CameraXutils
import it.unibo.kickify.ui.KickifyRoute
import it.unibo.kickify.ui.composables.AppBar
import it.unibo.kickify.ui.composables.BottomBar
import it.unibo.kickify.ui.composables.EmailRoundedTextField
import it.unibo.kickify.ui.composables.PasswordRoundedTextField
import it.unibo.kickify.ui.composables.UsernameRoundedTextField
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import it.unibo.kickify.ui.theme.BluePrimary
import it.unibo.kickify.ui.theme.GhostWhite
import it.unibo.kickify.ui.theme.LightGray
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    cameraXutils: CameraXutils,
    settingsViewModel: SettingsViewModel
){
    Scaffold(
        topBar = {
            AppBar(
                navController,
                title = stringResource(R.string.profileScreen_title)
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            val profileScreenModifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp)

            ProfileImageWithChangeButton(navController, cameraXutils, settingsViewModel)

            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 4.dp),
                text = "Mario Rossi",
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.profileScreen_fullName),
                modifier = profileScreenModifier
            )
            UsernameRoundedTextField(modifier = profileScreenModifier)
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = stringResource(R.string.emailAddress),
                modifier = profileScreenModifier
            )
            EmailRoundedTextField(
                modifier = profileScreenModifier,
                placeholderString = stringResource(R.string.emailAddress)
            ) { }
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                stringResource(R.string.password),
                modifier = profileScreenModifier
            )
            PasswordRoundedTextField(modifier = profileScreenModifier) { }

            Button(
                onClick = { navController.navigate(KickifyRoute.MyOrders)}
            ) {
                Text(stringResource(R.string.myordersScreen_title))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageWithChangeButton(
    navController: NavController,
    cameraXutils: CameraXutils,
    settingsViewModel: SettingsViewModel
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val userImgState by settingsViewModel.userImg.collectAsStateWithLifecycle()
    var imageUri by remember { mutableStateOf(userImgState.toUri()) }

    Log.i("KICKIFY", "imgURI:$imageUri")

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
        val userBitmap = cameraXutils.getBitmapFromUri(LocalContext.current, imageUri)
       // val userBitmap = cameraXutils.getBitmapFromFile(LocalContext.current)

        // if found user image saved in app cache
        if(userBitmap != null) {
            Image(
                bitmap = userBitmap.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
            )
        } else { // otherwise use user profile icon
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "",
                modifier = Modifier.size(140.dp) .clip(CircleShape)
                    .align(Alignment.TopCenter)
            )
        }

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
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                scope.launch { launcher.launch("image/*") }
                            }
                        ) {
                            Text(stringResource(R.string.profileScreen_choosePhotoFromGallery))
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { navController.navigate(KickifyRoute.TakeProfilePhoto) }
                        ) {
                            Text(stringResource(R.string.profileScreen_takePhoto))
                        }
                    }
                }
            }
        }
    }
}