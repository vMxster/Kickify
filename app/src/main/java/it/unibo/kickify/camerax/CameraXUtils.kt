package it.unibo.kickify.camerax

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CameraMode {
    CAPTURE, PREVIEW
}

class CameraXUtils(private val ctx: Context) {
    val permissions = arrayOf(Manifest.permission.CAMERA)

    fun hasRequiredPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                ctx, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun savePhotoInGallery(context: Context, uri: Uri) {
        val resolver = context.contentResolver
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val timestamp = formatter.format(Date())
        val fileName = "photo_$timestamp.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
        val imageUri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { outputUri ->
            resolver.openOutputStream(outputUri)?.use { outputStream ->
                resolver.openInputStream(uri)?.copyTo(outputStream)
            }
        }
    }

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } else {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
        } catch (e: Exception) {
            println("getBitmapFromUri: Error getting bitmap from URI: $uri - \n$e")
            null
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        val success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        if (success) {
            return outputStream.toByteArray()
        }
        return null
    }

    /***** OLD UNUSED  ******

    private val defaultUserProfileImageFilename = "userImg.png"

    val cameraSelector = CameraSelector.Builder()
    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
    .build()

    private val resolutionSelector = ResolutionSelector.Builder()
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
    ):Uri {
    val file = File(context.filesDir, fileName)
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
    return Uri.fromFile(file)
    }

    private fun getBitmapFromFile(
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

    val cameraProvider = ProcessCameraProvider.getInstance(ctx).get()

    val extensionsManager = ExtensionsManager.getInstanceAsync(
    ctx, cameraProvider
    ).get()

     ********************************************/
}