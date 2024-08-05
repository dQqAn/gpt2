package repositories

import kotlinx.coroutines.flow.StateFlow

interface FirebaseMessageRepository {
    val currentUserID: String
    val currentUserMail: String
    val friendID: StateFlow<String?>
    val messageID: String?
    fun otherUserID(mail: String)
    fun getMailtoFirestore(mail: String)
}