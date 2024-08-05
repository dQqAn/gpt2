package repositories

interface FirebaseMessageRepository {
    val currentUserID: String
    val currentUserMail: String
    val friendMail: String?
    val messageID: String?
    fun otherUserID(mail: String)
    fun getMailtoFirestore(mail: String)
}