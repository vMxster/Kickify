package it.unibo.kickify.camerax

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LightingColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream

class CameraXutils(private val ctx: Context) {

    private val defaultUserProfileImageFilename = "userImg.png"

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    val resolutionSelector = ResolutionSelector.Builder()
        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
        .build()

    val cameraPreview = Preview.Builder()
        .setResolutionSelector(resolutionSelector)
        .build()

    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .setResolutionSelector(resolutionSelector)
        .setPostviewEnabled(true)
        .setPostviewResolutionSelector(resolutionSelector)
        .build()

    fun saveBitmapToFile(
        context: Context, bitmap: Bitmap,
        fileName: String = defaultUserProfileImageFilename
    ) {
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    fun getBitmapFromFile(
        context: Context,
        fileName: String = defaultUserProfileImageFilename
    ): Bitmap? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    /*val cameraProvider = ProcessCameraProvider.getInstance(ctx).get()

    val extensionsManager = ExtensionsManager.getInstanceAsync(
        ctx, cameraProvider
    ).get()*/

    val permissions = arrayOf(Manifest.permission.CAMERA)

    private val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    val availableEffects: Map<CameraXeffects, Boolean> = mapOf()

    fun applyFilter(filterName: CameraXeffects) : Bitmap? {
        var filteredBitmap : Bitmap? = null
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
            val bitmap = imageProxy.toBitmap()
            when(filterName){
                CameraXeffects.NONE -> { }
                CameraXeffects.HDR -> {

                }
                CameraXeffects.BLACK_WHITE -> {
                    filteredBitmap = convertToBlackWhite(bitmap)
                }
                CameraXeffects.BOKEH -> {

                }
            }
            imageProxy.close()
        }
        return filteredBitmap
    }

    fun hasRequiredPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                ctx, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getCorrectionMatrix(image: ImageProxy, lensFacing: Int): Matrix {
        return Matrix().apply {
            // set photo orientation as the device
            postRotate(image.imageInfo.rotationDegrees.toFloat())

            if(lensFacing == CameraSelector.LENS_FACING_FRONT){
                // no mirrored photo from front camera
                postScale(-1f, 1f)
            }
        }
    }
}

/** Function to apply Black/White effect */
private fun convertToBlackWhite(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val grayscaleBitmap = createBitmap(width, height)

    val canvas = Canvas(grayscaleBitmap)
    val paint = Paint()
    val filter = LightingColorFilter(0xFF7F7F7F.toInt(), 0x00000000)
    paint.colorFilter = filter
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return grayscaleBitmap
}