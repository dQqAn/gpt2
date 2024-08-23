package repositories

import AnswerEntity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface FirebaseMessageRepository {
    val currentUserID: String
    val currentUserMail: String
    val friendID: StateFlow<String?>
    val messageID: String?
    val messageList: MutableStateFlow<List<AnswerEntity?>>
    val filteredList: MutableStateFlow<List<String?>>
    val otherUserID: MutableStateFlow<String?>

    fun otherUserID(mail: String?)
    fun getMailtoFirestore(mail: String)

    suspend fun addAnswer(content: String, contentType: String, chatID: String, senderID: String, receiverID: String)
    suspend fun getAnswer(chatID: String, senderID: String, receiverID: String?)
    suspend fun uploadFiles(
        files: MutableState<List<File?>>,
        contentType: String,
        chatID: String,
        senderID: String,
        receiverID: String,
        viewModelScope: CoroutineScope
    )

    fun getOnlineFile(path: String, list: MutableState<ByteArray?>): ByteArray?

    @Composable
    fun takePermission(
        openGallery: MutableState<Boolean>,
        showRationalDialog: MutableState<Boolean>,
    )

    @Composable
    fun launchGallery(
        openGallery: MutableState<Boolean>,
        showRationalDialog: MutableState<Boolean>,
        selectedImages: MutableState<List<File?>>
    )
}