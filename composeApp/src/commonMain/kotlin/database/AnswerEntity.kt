import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("answers")
data class AnswerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "messageID")
    val messageID: String = "",
    @ColumnInfo("chatID")
    val chatID: String,
    @ColumnInfo("role")
    val role: String,
    @ColumnInfo("content")
    val content: String,
    @ColumnInfo("senderID")
    val senderID: String,
    @ColumnInfo("receiverID")
    val receiverID: String,
    @ColumnInfo("date")
    val date: String
)