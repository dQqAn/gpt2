import android.Manifest
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.AndroidActivityViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.FirebaseMessageRepository
import util.GetCurrentDate
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

actual class FirebaseMessageRepositoryImp(
//    val fileUploadListener: FileUploadListener
) : FirebaseMessageRepository, KoinComponent {

    /*actual interface FileUploadListener {
        actual fun onFileUploadError(error: String)
        actual fun onFileUploadResults(
            content: String,
            contentType: String,
            chatID: String,
            senderID: String,
            receiverID: String
        )
    }*/

    override val chatID: MutableStateFlow<String?> = MutableStateFlow(null)
    override val isNewChat: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val database: AnswerDatabase by inject()
    private val messageRepository: MessageRepository by inject()
    private val context: Context by inject()
    private val androidActivityViewModel: AndroidActivityViewModel by inject()

    override val filteredList: MutableStateFlow<List<String?>> = MutableStateFlow(emptyList())
    override val otherUserID: MutableStateFlow<String?> = MutableStateFlow(null)

    override val currentUserID: String = auth.currentUser!!.uid
    override val currentUserMail: String = auth.currentUser!!.email!!
    override val friendID: StateFlow<String?> = otherUserID.asStateFlow()

    override val messageList: MutableStateFlow<List<AnswerEntity?>> = MutableStateFlow(emptyList())

    private val firestore = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    private val databaseMessaging: DatabaseReference =
        FirebaseDatabase.getInstance("https://gpt-chat-6c38c-default-rtdb.europe-west1.firebasedatabase.app").reference.child(
            "Message"
        )

    override fun otherUserID(mail: String?) {
        if (mail != null) {
            firestore.collection("Users").document(mail).addSnapshotListener { value, error ->
                if (error != null) {
                    println(error.message)
                    return@addSnapshotListener
                }
                if (value?.exists() == true) {
                    val data = value.get("Basic Information") as? HashMap<*, *>
                    otherUserID.update {
                        data?.get("id").toString()
                    }
                }
            }
        } else {
            otherUserID.update {
                null
            }
        }
    }

    override fun getMailtoFirestore(mail: String) {
        firestore.collection("Users").orderBy(FieldPath.documentId()).startAt(mail).endAt(mail + "\uf8ff")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println(error.message)
                    return@addSnapshotListener
                }
                if (!value?.documents.isNullOrEmpty()) {
                    val tempList = ArrayList<String?>()
                    for (data in value!!.documentChanges) {
                        if (data?.document?.id != null) {
                            tempList.add(data.document.id)
                            filteredList.update {
                                tempList
                            }
                        }
                    }
                }
            }
    }

    override suspend fun addAnswer(
        content: String, contentType: String, chatID: String, senderID: String, receiverID: String, id: Int
    ) {
        val key = databaseMessaging.child(senderID).child(receiverID).push().key
        if (key != null) {
            val messageObject = AnswerEntity(
                id = id,
                chatID = chatID,
                role = "user",
                contentType = contentType,
                content = content,
                senderID = senderID,
                receiverID = receiverID,
                date = GetCurrentDate(),
                messageID = key
            )

            database.answerDao().addAnswer(messageObject)

            databaseMessaging.child(senderID).child(receiverID).child(key).setValue(messageObject)
                .addOnSuccessListener {
                    if (receiverID != senderID) {
                        databaseMessaging.child(receiverID).child(senderID).child(key).setValue(messageObject)
                            .addOnSuccessListener {

                            }.addOnFailureListener {
                                println(it.message)
                            }
                    }
                }.addOnFailureListener {
                    println(it.message)
                }
        } else {
            println("Error getting key value!")
        }
    }

    override suspend fun getAnswer(chatID: String, senderID: String, receiverID: String?) {
        receiverID?.let {
            databaseMessaging.child(senderID).child(receiverID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val message: AnswerEntity? = postSnapshot?.getValue(AnswerEntity::class.java)
                        if (message != null) {
                            val list = messageList.value.map {
                                it?.messageID
                            }
                            if (!list.contains(message.messageID)) {
                                messageList.update {
                                    messageList.value + message
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
        }
    }

    override fun getOnlineFile(path: String, byteArray: MutableState<ByteArray?>) {
        val oneMegabyte: Long = 1024 * 1024
        val islandRef = storageRef.child(path)
        islandRef.getBytes(oneMegabyte).addOnSuccessListener { bArray ->
            byteArray.value = bArray
        }.addOnFailureListener {
            println(it.message)
        }
    }

    override suspend fun uploadFiles(
        files: MutableState<List<File?>>,
        contentType: String,
        chatID: String,
        senderID: String,
        receiverID: String,
        viewModelScope: CoroutineScope, id: Int
    ) {
        if (files.value.isNotEmpty()) {
            for ((index, item) in files.value.withIndex()) {
                val imageRef = storageRef.child("images/${chatID}/${item!!.name}")
                val stream = withContext(Dispatchers.IO) {
                    FileInputStream(item)
                }
                val uploadTask = imageRef.putStream(stream)
                uploadTask.addOnSuccessListener {
                    println("${item.name} uploaded.")
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            addAnswer(it.storage.path, contentType, chatID, senderID, receiverID, id)
                        }
                    }
//                    fileUploadListener.onFileUploadResults(it.storage.path, contentType, chatID, senderID, receiverID)
                }.addOnFailureListener { taskSnapshot ->
                    println(taskSnapshot.message)
                }
            }
            files.value = listOf()
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun takePermission(openGallery: MutableState<Boolean>, showRationalDialog: MutableState<Boolean>) {
        if (showRationalDialog.value) {
            alertDialog(showRationalDialog)
        }

        val multiplePermissionsState = rememberMultiplePermissionsState(permissionList)

        LaunchedEffect(openGallery.value) {
            if (openGallery.value) {
                if (!multiplePermissionsState.allPermissionsGranted) {
                    if (!multiplePermissionsState.shouldShowRationale) {
                        showRationalDialog.value = true
                    } else {
                        multiplePermissionsState.launchMultiplePermissionRequest()
                    }
                }
                openGallery.value = false
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun launchGallery(
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
//            uploadFile(selectedImages)
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

    @Throws(IOException::class)
    private fun urisToFiles(uris: List<Uri>): List<File> {
        val files = java.util.ArrayList<File>(uris.size)
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
    private fun File.remove() {
        if (isDirectory) {
            val entries = listFiles()
            if (entries != null) {
                for (entry in entries) {
                    entry.remove()
                }
            }
        }
        delete()
    }

    fun cleanUpWorkingDirectory() {
        requireTemporaryDirectory().remove()
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