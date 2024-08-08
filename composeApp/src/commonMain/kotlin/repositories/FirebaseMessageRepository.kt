package repositories

import AnswerEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface FirebaseMessageRepository {
    val currentUserID: String
    val currentUserMail: String
    val friendID: StateFlow<String?>
    val messageID: String?
    val messageList: MutableStateFlow<List<AnswerEntity?>>

    fun otherUserID(mail: String?)
    fun getMailtoFirestore(mail: String)

    suspend fun addAnswer(message: String, chatID: String, senderID: String, receiverID: String)
    suspend fun getAnswer(chatID: String, senderID: String, receiverID: String?)
}