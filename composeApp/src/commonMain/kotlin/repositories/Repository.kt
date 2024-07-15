import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun askQuestion(
        prevQuestion: List<Message>,
        chatID: String,
        question: String,
        senderID: String,
        receiverID: String
    ): BaseModel<Answer>

    suspend fun getMessages(chatID: String): Flow<List<Message>>

    suspend fun addAnswer(answer: Message, senderID: String, receiverID: String)

    //    suspend fun getChats(senderID: String, receiverID: String):Flow<List<AnswerEntity>>
    suspend fun getChats(): Flow<List<String>>

    suspend fun deleteTable()
}