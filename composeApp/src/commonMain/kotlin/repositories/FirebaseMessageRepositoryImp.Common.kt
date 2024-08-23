import repositories.FirebaseMessageRepository

expect class FirebaseMessageRepositoryImp : FirebaseMessageRepository {
    interface FileUploadListener {
        fun onFileUploadError(error: String)
        fun onFileUploadResults(
            content: String,
            contentType: String,
            chatID: String,
            senderID: String,
            receiverID: String
        )
    }
}