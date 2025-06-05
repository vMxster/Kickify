package it.unibo.kickify.camerax

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.extensions.ExtensionMode.BOKEH
import androidx.camera.extensions.ExtensionMode.FACE_RETOUCH
import androidx.camera.extensions.ExtensionMode.HDR
import androidx.camera.extensions.ExtensionMode.NIGHT
import androidx.camera.extensions.ExtensionMode.NONE
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BlurOn
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FaceRetouchingNatural
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.FlipCameraAndroid
import androidx.compose.material.icons.outlined.HdrOn
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.NoFlash
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import it.unibo.kickify.R
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun TakePhotoCameraScreen(
    navController: NavController,
    cameraXUtils: CameraXUtils,
    settingsViewModel: SettingsViewModel
) {
    var cameraMode by remember { mutableStateOf(CameraMode.CAPTURE) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var isFlashOn by remember { mutableStateOf(false) }
    var selectedEffect by remember { mutableIntStateOf(NONE) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userEmail by settingsViewModel.userId.collectAsStateWithLifecycle()

    if (cameraMode == CameraMode.CAPTURE) {
        CameraCaptureScreen(
            onPhotoCaptured = { uri ->
                capturedImageUri = uri
                cameraMode = CameraMode.PREVIEW
            },
            onSwitchCamera = { lensFacing =
                if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
                else CameraSelector.LENS_FACING_BACK
            },
            onToggleFlash = { isFlashOn = !isFlashOn },
            onApplyEffect = { selectedEffect = it },
            lensFacing = lensFacing,
            isFlashOn = isFlashOn,
            selectedEffect = selectedEffect,
            context = context
        )
    } else {
        PhotoPreviewScreen(
            imageUri = capturedImageUri,
            onRetakePhoto = {
                capturedImageUri = null
                cameraMode = CameraMode.CAPTURE
            },
            onSavePhoto = { // if captureimageuri != null
                capturedImageUri?.let {
                    cameraXUtils.savePhotoInGallery(it)
                    println("captured img uri: $it")

                    // save image in remote server and set as user image
                    scope.launch {
                        val bitmapImg = cameraXUtils.getBitmapFromCapture(it)
                        val mimeType = cameraXUtils.getImageMimeType(it)

                        if (bitmapImg != null) {
                            val byteArray = cameraXUtils.bitmapToByteArray(bitmapImg)
                            if (byteArray != null) {
                                settingsViewModel.setUserImg(userEmail, byteArray, mimeType)
                            }
                        }
                    }
                }
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun CameraCaptureScreen(
    onPhotoCaptured: (Uri) -> Unit,
    onSwitchCamera: () -> Unit,
    onToggleFlash: () -> Unit,
    onApplyEffect: (Int) -> Unit,
    lensFacing: Int,
    isFlashOn: Boolean,
    selectedEffect: Int,
    context: Context
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val previewView = remember { PreviewView(context) }
    var isFlashAvailable = false
    val isEffectAvailableFrontCamera: MutableMap<Int, Boolean> = mutableMapOf()
    val isEffectAvailableBackCamera: MutableMap<Int, Boolean> = mutableMapOf()

    fun getAvailableEffects(): Map<Int, Boolean>{
        return if(lensFacing == CameraSelector.LENS_FACING_BACK){
            isEffectAvailableBackCamera.toMap()
        } else isEffectAvailableFrontCamera.toMap()
    }

    LaunchedEffect(lensFacing, isFlashOn, selectedEffect) {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()

        val extensionsManager = ExtensionsManager.getInstanceAsync(context, cameraProvider).get()
        val baseCameraSelector =
            if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

        val cameraSelector =
            if (extensionsManager.isExtensionAvailable(baseCameraSelector, selectedEffect)) {
                extensionsManager.getExtensionEnabledCameraSelector(baseCameraSelector, selectedEffect)
            } else {
                baseCameraSelector
            }

        val preview = Preview.Builder().build().apply {
            surfaceProvider = previewView.surfaceProvider
        }
        imageCapture = ImageCapture.Builder().build()

        if(isEffectAvailableBackCamera.isEmpty() || isEffectAvailableFrontCamera.isEmpty()) {
            val modes = listOf(HDR, BOKEH, NIGHT, FACE_RETOUCH)
            for (mode in modes) {
                isEffectAvailableFrontCamera[mode] = extensionsManager.isExtensionAvailable(
                    CameraSelector.DEFAULT_FRONT_CAMERA, mode
                )
                isEffectAvailableBackCamera[mode] = extensionsManager.isExtensionAvailable(
                    CameraSelector.DEFAULT_BACK_CAMERA, mode
                )
            }
            println("back camera: $isEffectAvailableBackCamera"+
                    "\nfront camera: $isEffectAvailableFrontCamera")
        }

        try{
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture
            )
            isFlashAvailable = camera.cameraInfo.hasFlashUnit()
        } catch (e: Exception){
            Log.e("CameraX", "CameraBindingError: ${e.message}")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            ExpandableButtonRow(
                onApplyEffect = onApplyEffect,
                availableEffects = getAvailableEffects()
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(
                onClick = { onToggleFlash() },
                enabled = isFlashAvailable
            ) {
                if(isFlashAvailable){
                    CameraIconVector(
                        icon =
                            if(isFlashOn) Icons.Outlined.FlashOff
                            else Icons.Outlined.FlashOn,
                        contentDescription =
                            if(isFlashOn) stringResource(R.string.disableFlash)
                            else stringResource(R.string.enableFlash)
                    )
                } else {
                    CameraIconVector(
                        icon = Icons.Outlined.NoFlash,
                        contentDescription = stringResource(R.string.flashUnavailable)
                    )
                }
            }
            IconButton(onClick = {
                val photoFile = File(context.externalCacheDir, "${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                imageCapture?.takePicture(
                    outputOptions, ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            onPhotoCaptured(Uri.fromFile(photoFile))
                        }
                        override fun onError(exception: ImageCaptureException) {}
                    }
                )
            }) {
                CameraIconVector(icon = Icons.Outlined.PhotoCamera,
                    contentDescription = stringResource(R.string.takePhoto)
                )
            }
            IconButton(
                onClick = { onSwitchCamera() }
            ) {
                CameraIconVector(icon = Icons.Outlined.FlipCameraAndroid,
                    contentDescription = stringResource(R.string.switchCamera)
                )
            }
        }
    }
}

@Composable
fun PhotoPreviewScreen(
    imageUri: Uri?,
    onRetakePhoto: () -> Unit,
    onSavePhoto: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = stringResource(R.string.photoPreview),
                modifier = Modifier.fillMaxSize(),
                placeholder = rememberVectorPainter(Icons.Outlined.Photo),
                contentScale = ContentScale.Fit
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            IconButton(onClick = { onRetakePhoto() }
            ) {
                CameraIconVector(icon = Icons.Outlined.Replay,
                    contentDescription = stringResource(R.string.retry)
                )
            }
            IconButton(onClick = { onSavePhoto() }
            ) {
                CameraIconVector(icon = Icons.Outlined.Check,
                    contentDescription = stringResource(R.string.savePhoto)
                )
            }
        }
    }
}

@Composable
fun ExpandableButtonRow(
    onApplyEffect: (Int) -> Unit,
    availableEffects: Map<Int, Boolean>
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = isExpanded
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                fun isEffectAvailable(key: Int): Boolean{
                    return availableEffects.containsKey(key) && availableEffects.getValue(key)
                }

                // buttons enabled only if effect is available
                IconButton(
                    enabled = isEffectAvailable(HDR),
                    onClick = { onApplyEffect(HDR) }
                ) {
                    CameraIconVector(
                        icon = Icons.Outlined.HdrOn,
                        contentDescription = "HDR"
                    )
                }

                IconButton(
                    enabled = isEffectAvailable(NIGHT),
                    onClick = { onApplyEffect(NIGHT) }
                ) {
                    CameraIconVector(
                        icon = Icons.Outlined.ModeNight,
                        contentDescription = "Night Mode"
                    )
                }

                IconButton(onClick = { onApplyEffect(BOKEH) }
                ) {
                    CameraIconVector(
                        icon = Icons.Outlined.BlurOn,
                        contentDescription = "Bokeh"
                    )
                }
            }
        }

        IconButton(onClick = { isExpanded = !isExpanded }) {
            CameraIconVector(
                icon = if (isExpanded) Icons.Outlined.Close else Icons.Outlined.FaceRetouchingNatural,
                contentDescription = "Toggle Menu"
            )
        }
    }
}

@Composable
fun CameraIconVector(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
){
    Icon(modifier = modifier.size(32.dp),
        imageVector = icon,
        contentDescription = contentDescription
    )
}