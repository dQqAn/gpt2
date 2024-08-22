package org.example.project

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalPermissionsApi::class)
class AndroidActivityViewModel() : ViewModel(), KoinComponent {
    val activity: MutableState<Activity?> = mutableStateOf(null)

    private val context: Context by inject()

    private val permissionList = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
    ) + listOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Manifest.permission.READ_MEDIA_IMAGES
            Manifest.permission.READ_MEDIA_VIDEO
            Manifest.permission.READ_MEDIA_AUDIO
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
            Manifest.permission.READ_MEDIA_VIDEO
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    @Composable
    fun permissionManager(
        openGallery: MutableState<Boolean>,
        showRationalDialog: MutableState<Boolean>,
        selectedImages: MutableState<List<File?>>
    ) {
        if (showRationalDialog.value) {
            alertDialog(showRationalDialog)
        }

        val multiplePermissionsState = rememberMultiplePermissionsState(permissionList)

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uri ->
            // uri to bitmap
            /*val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri.get(0)))
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri.get(0))
            }*/
            selectedImages.value = urisToFiles(uri)
        }

        LaunchedEffect(openGallery.value) {
            if (openGallery.value) {
                if (multiplePermissionsState.allPermissionsGranted) {
                    launcher.launch("image/*")
                } else {
                    if (!multiplePermissionsState.shouldShowRationale) {
                        showRationalDialog.value = true
                    } else {
                        multiplePermissionsState.launchMultiplePermissionRequest()
                    }
                }
                openGallery.value = false
            }
        }

        LaunchedEffect(multiplePermissionsState.allPermissionsGranted) {
            if (multiplePermissionsState.allPermissionsGranted && openGallery.value) {
                launcher.launch("image/*")
            }
        }
    }

    @Throws(IOException::class)
    fun urisToFiles(uris: List<Uri>): List<File> {
        val files = ArrayList<File>(uris.size)
        for (uri in uris) {
            val file = createTempFile(requireTemporaryDirectory())
            writeUriToFile(uri, file)
            files.add(file)
        }
        return files
    }

    @Throws(IOException::class)
    private fun createTempFile(root: File): File {
        root.mkdirs() // make sure that the directory exists
        val date = SimpleDateFormat(DATE_FORMAT_TEMPLATE, Locale.getDefault()).format(Date())
        val filePrefix = IMAGE_NAME_TEMPLATE.format(date)
        return File.createTempFile(filePrefix, JPG_EXTENSION, root)
    }

    @Throws(IOException::class)
    private fun writeUriToFile(target: Uri, destination: File) {
        val inputStream = context.contentResolver.openInputStream(target)!!
        val outputStream = FileOutputStream(destination)
        inputStream.use { input ->
            outputStream.use { out ->
                input.copyTo(out)
            }
        }
    }

    private fun requireTemporaryDirectory(): File {
        // don't need to read / write permission for this directory starting from android 19
        val pictures = context.getExternalFilesDir(DIRECTORY_PICTURES)!!
        return File(pictures, TEMPORARY_DIRECTORY_NAME)
    }

    // there is no build in function for deleting folders <3
    private fun File.Remove() {
        if (isDirectory) {
            val entries = listFiles()
            if (entries != null) {
                for (entry in entries) {
                    entry.Remove()
                }
            }
        }
        delete()
    }

    fun cleanUpWorkingDirectory() {
        requireTemporaryDirectory().Remove()
    }

    companion object {
        private const val TEMPORARY_DIRECTORY_NAME = "Temporary"
        private const val DATE_FORMAT_TEMPLATE = "yyyyMMdd_HHmmss"
        private const val IMAGE_NAME_TEMPLATE = "IMG_%s_"
        private const val JPG_EXTENSION = ".jpg"
    }

    @Composable
    private fun alertDialog(showRationalDialog: MutableState<Boolean>) {
        AlertDialog(
            onDismissRequest = {
                showRationalDialog.value = false
            },
            title = {
                Text(
                    text = "Permission",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "The notification is important for this app. Please grant the permission.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationalDialog.value = false
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(context, intent, null)

                    }) {
                    Text("OK", style = TextStyle(color = Color.Black))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRationalDialog.value = false
                    }) {
                    Text("Cancel", style = TextStyle(color = Color.Black))
                }
            },
        )
    }
}