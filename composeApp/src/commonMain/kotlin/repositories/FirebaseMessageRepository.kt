package repositories

interface FirebaseMessageRepository {
    val currentUserID: String
    val currentUserMail: String
    val friendMail: String?
    val otherUserID: String?
    val messageID: String?
    fun getMailtoFirestore(mail: String)
}