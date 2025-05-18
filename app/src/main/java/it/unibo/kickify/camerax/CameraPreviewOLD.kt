package it.unibo.kickify.camerax

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import it.unibo.kickify.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import androidx.camera.core.Preview as CameraPreview

@Composable
fun TakePhotoCameraScreenOLD(
    navController: NavController,
    cameraXutils: CameraXutils,
    settingsViewModel: SettingsViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // preview container
    val previewView = remember { PreviewView(context) }
    val preview = remember { cameraXutils.cameraPreview }

    // capture image container
    val imageCapture = remember { cameraXutils.imageCapture }

    CameraContainer(context, navController, cameraXutils, previewView,
        imageCapture, lifecycleOwner, preview, settingsViewModel)
}

@Composable
private fun CameraContainer(
    context: Context,
    navController: NavController,
    cameraXutils: CameraXutils,
    previewView: PreviewView,
    imageCapture: ImageCapture,
    lifecycleOwner: LifecycleOwner,
    preview: Preview,
    settingsViewModel: SettingsViewModel
) {
    var captureProgress by remember { mutableIntStateOf(0) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var finalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }

    val coroutineScope = rememberCoroutineScope()

    val cameraSelector = cameraXutils.cameraSelector
    /*val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()*/

    LaunchedEffect(Unit) {
        bindCamera(context, lifecycleOwner, preview, imageCapture, previewView, cameraSelector)
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        // if previewbitmap not null and finalbitmap == null
        if(finalBitmap == null) {
            previewBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Preview",
                    modifier = Modifier.fillMaxSize()
                )
            } ?: AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        }

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            if (captureProgress > 0) {
                Text(
                    "Don't move your camera ($captureProgress%)",
                    color = Color.White,
                    modifier = Modifier
                        .padding(2.dp)
                        .background(Color.Black)
                )
            } else {
                if(finalBitmap == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(
                            onClick = {
                                imageCapture.takePicture(
                                    Dispatchers.Default.asExecutor(),
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(image: ImageProxy) {
                                            captureProgress = 0
                                            previewBitmap = null

                                            finalBitmap = Bitmap.createBitmap(
                                                image.toBitmap(),
                                                0,
                                                0,
                                                image.width,
                                                image.height,
                                                cameraXutils.getCorrectionMatrix(image, lensFacing),
                                                true
                                            )
                                            image.close()
                                        }

                                        override fun onPostviewBitmapAvailable(bitmap: Bitmap) {
                                            previewBitmap = bitmap
                                        }

                                        override fun onCaptureProcessProgressed(progress: Int) {
                                            captureProgress = progress
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            Log.e(
                                                "CameraPreview",
                                                "Error capturing image",
                                                exception
                                            )
                                            captureProgress = 0
                                            previewBitmap = null
                                        }
                                    }
                                )
                            }
                        ) {
                            CameraIconButton(icon = Icons.Outlined.PhotoCamera,
                                contentDescription = "Take picture")
                        }
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                        CameraSelector.LENS_FACING_FRONT
                                    } else {
                                        CameraSelector.LENS_FACING_BACK
                                    }
                                    bindCamera(context, lifecycleOwner, preview, imageCapture, previewView, cameraSelector)

                                }
                            }
                        ) {
                            CameraIconButton(icon = Icons.Outlined.Cameraswitch,
                                contentDescription = "Switch Camera")
                        }
                    }
                } else {
                    finalBitmap?.let { bitmap ->
                        Row (
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            IconButton(
                                onClick = { finalBitmap = null }
                            ) {
                                CameraIconButton(icon = Icons.Outlined.Replay,
                                    contentDescription = "Retry")
                            }
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val imgUri = cameraXutils.saveBitmapToFile(
                                            context = context, bitmap = bitmap
                                        )
                                        settingsViewModel.setUserImg(imgUri.toString())
                                        navController.popBackStack() // go back to profile page
                                    }
                                }
                            ) {
                                CameraIconButton(icon = Icons.Outlined.Check,
                                    contentDescription = "Save")
                            }
                        }
                    }
                }
            }
        }

        finalBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Final",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun CameraIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
){
    Icon(modifier = modifier.size(38.dp),
        imageVector = icon,
        contentDescription = contentDescription
    )
}

private fun bindCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    preview: CameraPreview,
    imageCapture: ImageCapture,
    previewView: PreviewView,
    cameraSelector: CameraSelector
) {
    val cameraProvider = ProcessCameraProvider.getInstance(context).get()

    val extensionsManager = ExtensionsManager.getInstanceAsync(
        context, cameraProvider
    ).get()

    cameraProvider.unbindAll()

    try {
        if (extensionsManager.isExtensionAvailable(cameraSelector, ExtensionMode.NIGHT)) {
            val nightCameraSelector = extensionsManager.getExtensionEnabledCameraSelector(
                cameraSelector, ExtensionMode.NIGHT
            )

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                nightCameraSelector,
                preview,
                imageCapture
            )
        } else {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }

        preview.surfaceProvider = previewView.surfaceProvider
    } catch (e: Exception) {
        Log.e("CameraPreview", "Error binding camera", e)
    }
}
