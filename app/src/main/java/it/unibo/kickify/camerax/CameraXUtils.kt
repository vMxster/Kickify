package it.unibo.kickify.camerax

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    fun savePhotoInGallery(uri: Uri) {
        val resolver = ctx.contentResolver
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

    suspend fun getBitmapFromCapture(uri: Uri): Bitmap? {
        val imageLoader = ctx.imageLoader
        val request = ImageRequest.Builder(ctx)
            .data(uri)
            .allowHardware(false)
            .build()

        val result: ImageResult = withContext(Dispatchers.IO) {
            imageLoader.execute(request)
        }
        return when (result) {
            is SuccessResult -> {
                (result.drawable as? BitmapDrawable)?.bitmap
            }
            is ErrorResult -> {
                result.throwable.printStackTrace()
                null
            }
        }
    }

    fun getBitmapFromSelector(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(ctx.contentResolver, uri))
            } else {
                ctx.contentResolver.openInputStream(uri)?.use { inputStream ->
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

    fun getImageMimeType(imageUri: Uri): String {
        val mimeType: String? = if (imageUri.scheme == "content") {
            ctx.contentResolver.getType(imageUri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }
        return mimeType ?: "application/octet-stream"
    }
}