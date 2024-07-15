import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {

    @Insert
//    @Insert(entity = AnswerEntity::class)
    suspend fun addAnswer(answerEntity: AnswerEntity)

    @Query("SELECT * FROM `answers` WHERE chatID = :chatID")
    fun getAnswer(chatID: String): Flow<List<AnswerEntity>>

    //    @Query("SELECT * FROM `answers` WHERE chatID in (:senderID, :receiverID)")
//    fun getChats(senderID: String, receiverID: String): Flow<List<AnswerEntity>>
//    @Query("SELECT chatID FROM `answers`")
    @Query("SELECT DISTINCT chatID FROM `answers`")
    fun getChats(): Flow<List<String>>

    @Query("DELETE FROM answers")
    suspend fun deleteTable()
}