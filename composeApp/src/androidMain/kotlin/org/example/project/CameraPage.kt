import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment.getExternalStorageDirectory
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material.icons.sharp.CheckCircle
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorMatrixColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.navigation.NavController
import util.Localization
import viewmodel.CameraViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
actual fun CameraPage(navController: NavController, localization: Localization, cameraViewModel: CameraViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text(localization.back)
        }

        CameraView(onImageCaptured = { uri, fromGallery ->

        }, onError = { imageCaptureException ->
            println(imageCaptureException)
        },
            cameraViewModel = cameraViewModel
        )
    }
}

private sealed class CameraUIAction {
    data object OnCameraClick : CameraUIAction()
    data object OnGalleryViewClick : CameraUIAction()
    data object OnSwitchCameraClick : CameraUIAction()
}

@Composable
private fun CameraView(
    onImageCaptured: (Uri, Boolean) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    cameraViewModel: CameraViewModel,
) {

    val context = LocalContext.current
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onImageCaptured(uri, true)
    }

    CameraPreviewView(
        imageCapture,
        lensFacing,
        cameraViewModel
    ) { cameraUIAction ->
        when (cameraUIAction) {
            is CameraUIAction.OnCameraClick -> {
                imageCapture.takePicture(context, lensFacing, onImageCaptured, onError, cameraViewModel)
            }

            is CameraUIAction.OnSwitchCameraClick -> {
                lensFacing =
                    if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK
            }

            is CameraUIAction.OnGalleryViewClick -> {
                if (true == context.getOutputDirectory().listFiles()?.isNotEmpty()) {
                    galleryLauncher.launch("image/*")
                }
            }

            else -> {}
        }
    }
}

private fun Context.getOutputDirectory(): File {
    val mediaDir = getExternalStorageDirectory()?.let {
        File(it, "/Pictures/Camera").apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else this.filesDir
}

private fun ImageCapture.takePicture(
    context: Context,
    lensFacing: Int,
    onImageCaptured: (Uri, Boolean) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    cameraViewModel: CameraViewModel,
) {
    val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
    val PHOTO_EXTENSION = ".png"
    val outputDirectory = context.getOutputDirectory()
    // Create output file to hold the image
    val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

    //OutputFileOptions can be added for output image inside
    this.takePicture(ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            super.onCaptureSuccess(image)

            val rotationDegrees = image.imageInfo.rotationDegrees.toFloat()
            val bitmap = gelFilteredBitmap(image.toBitmap(), cameraViewModel.filterNumber.value, rotationDegrees)

            val fos = FileOutputStream(photoFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos) //https://stackoverflow.com/a/47971423
            fos.flush()
            fos.close()

            val savedUri = Uri.fromFile(photoFile)

            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(savedUri.toFile().extension)

            MediaScannerConnection.scanFile(
                context,
                arrayOf(savedUri.toFile().absolutePath),
                arrayOf(mimeType)
            ) { _, uri ->

            }

            onImageCaptured(savedUri, false)
        }

        override fun onError(exception: ImageCaptureException) {
            super.onError(exception)
            onError(exception)
        }
    })
}

private fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
    val height: Int = bmpOriginal.height
    val width: Int = bmpOriginal.width
    val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmpGrayscale)
    val paint = Paint()
    val cm = ColorMatrix()
    cm.setToSaturation(0f)
    val f = ColorMatrixColorFilter(cm)
    paint.colorFilter = f
    c.drawBitmap(bmpOriginal, 0f, 0f, paint.asFrameworkPaint())
    return bmpGrayscale
}

private fun gelFilteredBitmap(bitmap: Bitmap, filterNumber: Int, rotationDegrees: Float): Bitmap {
    return when (filterNumber) {
        0 -> {
            toGrayscale(bitmap)
        }

        else -> {
            bitmap
        }
    }
}

@Composable
private fun CameraPreviewView(
    imageCapture: ImageCapture,
    lensFacing: Int = CameraSelector.LENS_FACING_FRONT,
    cameraViewModel: CameraViewModel,
    cameraUIAction: (CameraUIAction) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    val imageView = remember { mutableStateOf(ImageView(context)) }

    val imageAnalysis = ImageAnalysis.Builder()
        // enable the following line if RGBA output is needed.
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//        .setTargetResolution(Size(1080, 1920))
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageRotationEnabled(true)
        .build()

    val previewView = remember { PreviewView(context) }

    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
        val localBitmap = imageProxy.toBitmap()
        val filterNumber = cameraViewModel.filterNumber.value

        imageView.value.setImageBitmap(gelFilteredBitmap(localBitmap, filterNumber, rotationDegrees))

        imageProxy.close()
    }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalysis,
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AndroidView({ imageView.value }, modifier = Modifier.fillMaxSize()) {

            }
            AndroidView({ previewView }, modifier = Modifier.fillMaxSize()) {

            }
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            CameraControls(cameraUIAction, cameraViewModel)
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

private fun createFile(baseFolder: File, format: String, extension: String) =
    File(
        baseFolder, SimpleDateFormat(format, Locale.US)
            .format(System.currentTimeMillis()) + extension
    )

@Composable
private fun CameraControls(
    cameraUIAction: (CameraUIAction) -> Unit,
    cameraViewModel: CameraViewModel,
) {

    var expanded by remember { mutableStateOf(false) }
    val listItems = arrayOf("Gray", "No Filter")

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CameraControl(
                Icons.Sharp.Menu,
                2,
                modifier = Modifier.size(64.dp),
                onClick = { expanded = true }
            )

            CameraControl(
                Icons.Sharp.Refresh,
                2,
                modifier = Modifier.size(64.dp),
                onClick = { cameraUIAction(CameraUIAction.OnSwitchCameraClick) }
            )

            CameraControl(
                Icons.Sharp.CheckCircle,
                2,
                modifier = Modifier
                    .size(64.dp)
                    .padding(1.dp)
                    .border(1.dp, Color.White, CircleShape),
                onClick = { cameraUIAction(CameraUIAction.OnCameraClick) }
            )

            CameraControl(
                Icons.Sharp.AddCircle,
                2,
                modifier = Modifier.size(64.dp),
                onClick = { cameraUIAction(CameraUIAction.OnGalleryViewClick) }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            val filterNumber = cameraViewModel.filterNumber
            listItems.forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(
                    text = { Text(itemValue) },
                    onClick = {
                        expanded = false
                        when (itemIndex) {
                            0 -> {
                                filterNumber.value = 0
                            }

                            1 -> {
                                filterNumber.value = 1
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun CameraControl(
    imageVector: ImageVector,
    contentDescId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector,
            contentDescription = "$contentDescId",
            modifier = modifier,
            tint = Color.White
        )
    }
}