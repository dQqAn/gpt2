package repositories

import AnswerDao
import AnswerEntity
import Message
import MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import util.GetCurrentDate

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

    override suspend fun getMessages(chatID: String): Flow<List<Message>> {
        return dao.getAnswer(chatID).map { value ->
            value.map { entity ->
                Message(
                    chatID = entity.chatID,
                    role = entity.role,
                    content = entity.content,
                    senderID = entity.senderID,
                    receiverID = entity.receiverID
                )
            }
        }
    }

    override suspend fun addAnswer(answer: Message, senderID: String, receiverID: String) {
        dao.addAnswer(
            AnswerEntity(
                chatID = answer.chatID!!, role = answer.role, content = answer.content,
                senderID = senderID, receiverID = receiverID, date = GetCurrentDate()
            )
        )
    }

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