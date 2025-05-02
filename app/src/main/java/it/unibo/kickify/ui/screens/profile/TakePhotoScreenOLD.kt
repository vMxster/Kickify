package it.unibo.kickify.ui.screens.profile

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.lang.Exception
import kotlin.math.hypot
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set
import kotlinx.coroutines.launch

// Estensione per convertire un ImageProxy (assumendo formato JPEG) in Bitmap
fun ImageProxy.convertToBitmap(): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

// Composable che gestisce l'anteprima della fotocamera e lo scatto della foto tramite CameraX
@Composable
fun CameraPreview(onImageCaptured: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val executor = ContextCompat.getMainExecutor(context)

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                // Creiamo un PreviewView, che mostrerà l'anteprima della fotocamera
                PreviewView(ctx).also { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        // Costruiamo il caso d'uso per la preview
                        val preview = Preview.Builder().build().also { it.surfaceProvider =
                            previewView.surfaceProvider }

                        // Costruiamo il caso d'uso per lo scatto dell'immagine
                        val imageCaptureUseCase = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = imageCaptureUseCase

                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCaptureUseCase
                            )
                        } catch (exc: Exception) {
                            exc.printStackTrace()
                        }
                    }, executor)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        // Pulsante di cattura sovrapposto all'anteprima
        IconButton(
            onClick = {
                imageCapture?.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bitmap = image.convertToBitmap()
                        onImageCaptured(bitmap)
                        image.close()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        exception.printStackTrace()
                    }
                })
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp)
        ) {
            Icon(Icons.Filled.Camera, contentDescription = "Scatta Foto")
        }
    }
}

// Filtro in Bianco e Nero: converte ogni pixel in scala di grigi usando il peso (0.299, 0.587, 0.114)
fun applyBlackAndWhiteFilter(original: Bitmap): Bitmap {
    val width = original.width
    val height = original.height
    val bmpGrayscale = createBitmap(width, height)

    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = original[x, y]
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)
            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            val newPixel = Color.rgb(gray, gray, gray)
            bmpGrayscale[x, y] = newPixel
        }
    }
    return bmpGrayscale
}

// Filtro Blur: applica un semplice box blur (media dei pixel in una finestra 3x3)
fun applyBlurFilter(original: Bitmap): Bitmap {
    val width = original.width
    val height = original.height
    val blurredBitmap = createBitmap(width, height)

    for (x in 0 until width) {
        for (y in 0 until height) {
            var sumR = 0
            var sumG = 0
            var sumB = 0
            var count = 0
            for (i in -1..1) {
                for (j in -1..1) {
                    val nx = x + i
                    val ny = y + j
                    if (nx in 0 until width && ny in 0 until height) {
                        val pixel = original[nx, ny]
                        sumR += Color.red(pixel)
                        sumG += Color.green(pixel)
                        sumB += Color.blue(pixel)
                        count++
                    }
                }
            }
            val avgR = sumR / count
            val avgG = sumG / count
            val avgB = sumB / count
            val newPixel = Color.rgb(avgR, avgG, avgB)
            blurredBitmap[x, y] = newPixel
        }
    }
    return blurredBitmap
}

// Filtro Bokeh: simula l'effetto bokeh mantenendo a fuoco il centro e applicando un blur progressivo verso i bordi.
// La funzione prima genera una versione blur dell'immagine e poi, per ogni pixel, mescola l'originale con la versione blur in funzione della distanza dal centro.
fun applyBokehFilter(original: Bitmap): Bitmap {
    val blurred = applyBlurFilter(original)
    val width = original.width
    val height = original.height
    val result = createBitmap(width, height)
    val centerX = width / 2f
    val centerY = height / 2f
    val maxDistance = hypot(centerX.toDouble(), centerY.toDouble())

    for (x in 0 until width) {
        for (y in 0 until height) {
            val dx = x - centerX
            val dy = y - centerY
            val distance = hypot(dx.toDouble(), dy.toDouble())
            // Il fattore aumenta con la distanza: al centro (in focus) il peso è 0, ai bordi tende a 1.
            var factor = (distance / maxDistance).toFloat()
            // Eleva al quadrato per una transizione più morbida
            factor *= factor
            val originalPixel = original[x, y]
            val blurredPixel = blurred[x, y]
            val r = (Color.red(originalPixel) * (1 - factor) + Color.red(blurredPixel) * factor).toInt()
            val g = (Color.green(originalPixel) * (1 - factor) + Color.green(blurredPixel) * factor).toInt()
            val b = (Color.blue(originalPixel) * (1 - factor) + Color.blue(blurredPixel) * factor).toInt()
            val blendedPixel = Color.rgb(r, g, b)
            result[x, y] = blendedPixel
        }
    }
    return result
}

// Composable principale che gestisce l'interfaccia utente: la preview della fotocamera, la visualizzazione
// dell'immagine catturata e la scelta dei filtri da applicare.
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraApp() {
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var filteredImage by remember { mutableStateOf<Bitmap?>(null) }
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Richiede il permesso per utilizzare la fotocamera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> cameraPermissionGranted = granted }
    )
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        // Verifica se il permesso è già concesso
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionGranted = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (!cameraPermissionGranted) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Permesso per l'uso della fotocamera non concesso")
            }
        } else {

            if (capturedImage == null) {
                CameraPreview { bitmap ->
                    capturedImage = bitmap
                    filteredImage = bitmap // Per default mostriamo l'immagine originale
                }
            } else {
                // Visualizza immagine catturata o filtrata
                Image(
                    bitmap = filteredImage?.asImageBitmap() ?: capturedImage!!.asImageBitmap(),
                    contentDescription = "Immagine catturata",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Pulsanti per applicare i filtri
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        filteredImage = capturedImage
                    }, enabled = !isProcessing) {
                        Text("Originale")
                    }
                    Button(onClick = {
                        capturedImage?.let { originalImage ->
                            coroutineScope.launch {
                                isProcessing = true
                                filteredImage = applyBlackAndWhiteFilter(originalImage)
                                isProcessing = false
                            }
                        }
                    }, enabled = !isProcessing) {
                        Text("B/N")
                    }
                    Button(onClick = {
                        capturedImage?.let { originalImage ->
                            coroutineScope.launch {
                                isProcessing = true
                                filteredImage = applyBokehFilter(originalImage)
                                isProcessing = false
                            }
                        }
                    }, enabled = !isProcessing) {
                        Text("Bokeh")
                    }
                    Button(onClick = {
                        capturedImage?.let { originalImage ->
                            coroutineScope.launch {
                                isProcessing = true
                                filteredImage = applyBlurFilter(originalImage)
                                isProcessing = false
                            }
                        }
                    }, enabled = !isProcessing) {
                        Text("Blur")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Pulsante per ricatturare una nuova immagine
                Button(
                    onClick = {
                        capturedImage = null
                        filteredImage = null
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Riprova")
                }
            }
        }
    }
}