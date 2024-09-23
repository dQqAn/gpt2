import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("model")
    val model: String = "gpt-3.5-turbo",
    @SerializedName("messages")
    val messages: List<Message>
)

data class Message(
    @SerializedName("chatID")
    val chatID: String?,
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("senderID")
    val senderID: String,
    @SerializedName("receiverID")
    val receiverID: String
)

val Message.fromUser: Boolean
    get() {
        return role == "user"
    }