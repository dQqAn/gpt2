package repositories

interface FirebaseMessageRepository {
    val currentUserID: String
    val otherUserID: String?
    val messageID: String?
}