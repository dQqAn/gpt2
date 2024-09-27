import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("answers")
data class AnswerEntity(
    @PrimaryKey
    @ColumnInfo(name = "messageID")
    val messageID: String = "",
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo("chatID")
    val chatID: String = "",
    @ColumnInfo("role")
    val role: String = "",
    @ColumnInfo("contentType")
    val contentType: String = "",
    @ColumnInfo("content")
    val content: String = "",
    @ColumnInfo("senderID")
    val senderID: String = "",
    @ColumnInfo("receiverID")
    val receiverID: String = "",
    @ColumnInfo("date")
    val date: String = ""
)

const val contentTypeMessage: String = "Message"

const val contentTypeImage: String = "Image"

val AnswerEntity.fromUser: Boolean
    get() {
        return role == "user"
    }