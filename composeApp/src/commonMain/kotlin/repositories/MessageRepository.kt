import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    /*suspend fun askQuestion(
        prevQuestion: List<Message>,
        chatID: String,
        question: String,
        senderID: String,
        receiverID: String
    ): BaseModel<Answer>*/

    suspend fun getMessages(chatID: String): Flow<List<AnswerEntity?>>

    suspend fun getMessage(chatID: String): AnswerEntity?

//    suspend fun addAnswer(answer: Message, senderID: String, receiverID: String)

    //    suspend fun getChats(senderID: String, receiverID: String):Flow<List<AnswerEntity>>
    suspend fun getChats(): Flow<List<String>>

    suspend fun deleteTable()

    suspend fun deleteChat(chatID: String)
}