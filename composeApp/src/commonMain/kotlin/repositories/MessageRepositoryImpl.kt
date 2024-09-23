package repositories

import AnswerDao
import AnswerEntity
import MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageRepositoryImpl(private val dao: AnswerDao) : MessageRepository {

    /*override suspend fun askQuestion(
        prevQuestion: List<Message>,
        chatID: String,
        question: String,
        senderID: String,
        receiverID: String
    ): BaseModel<Answer> {
        try {
            api.askQuestion(
                question = Question(
                    messages = prevQuestion + Message(
                        chatID = chatID,
                        role = "user",
                        content = question,
                        senderID = senderID,
                        receiverID = receiverID
                    )
                )
            ).also { response ->
                return if (response.isSuccessful) {
                    BaseModel.Success(data = response.body()!!)
                } else {
                    BaseModel.Error(response.errorBody()?.string().toString())
                }
            }
        } catch (e: Exception) {
            return BaseModel.Error(e.message.toString())
        }
    }*/

    override suspend fun getMessages(chatID: String): Flow<List<AnswerEntity?>> {
        return dao.getAnswer(chatID).map { value ->
            value.map { entity ->
                AnswerEntity(
                    chatID = entity.chatID,
                    role = entity.role,
                    contentType = entity.contentType,
                    content = entity.content,
                    senderID = entity.senderID,
                    receiverID = entity.receiverID,
                    date = entity.date,
                    messageID = entity.messageID
                )
            }
        }
    }

    override suspend fun getMessage(messageID: String): AnswerEntity? {
        val entity = dao.getMessage(messageID) ?: return null
        return AnswerEntity(
            chatID = entity.chatID,
            role = entity.role,
            contentType = entity.contentType,
            content = entity.content,
            senderID = entity.senderID,
            receiverID = entity.receiverID,
            date = entity.date,
            messageID = entity.messageID
        )
    }

    /*override suspend fun addAnswer(answer: Message, senderID: String, receiverID: String) {
        dao.addAnswer(
            AnswerEntity(
                chatID = answer.chatID!!, role = answer.role, content = answer.content,
                senderID = senderID, receiverID = receiverID, date = GetCurrentDate()
            )
        )
    }*/

    override suspend fun getChats(): Flow<List<String>> {
        return dao.getChats().map { value ->
            value.map { entity ->
                entity
            }
        }
    }

    override suspend fun deleteTable() {
        dao.deleteTable()
    }

    override suspend fun deleteChat(chatID: String) {
        dao.deleteChat(chatID = chatID)
    }
}