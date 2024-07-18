import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.bert.BertHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatViewModel : ViewModel(), KoinComponent {

    private val database: AppDatabase by inject()
    private val repository: Repository by inject()
    private val bertHelper: BertHelper by inject()

    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messages = _messages.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _chatID = mutableStateOf<String?>(null)
    val chatID = _chatID
    fun changeChatID(chatID: String?) {
        viewModelScope.launch {
            _chatID.value = chatID
        }
    }

    fun loadMessages() {
        viewModelScope.launch {
            repository.getMessages(_chatID.value!!).collect { data ->
                _messages.update { data }
            }
        }
    }

    init {
        /*val rnds = (0..10).random()
//        var titles: List<String> = emptyList()
        var content: String = ""
        //    var questions: List<String> = emptyList()
        val client = LoadDataSetClient()
        client.loadJson()?.let {
//            titles = it.getTitles()
            content = it.getContents()[rnds]
            //        questions = it.questions
        }
        println(content)*/
    }

    fun askQuestion(question: String, chatID: String, senderID: String, receiverID: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.answerDao().addAnswer(
                    answerEntity = AnswerEntity(
                        chatID = chatID,
                        role = "user",
                        content = question,
                        senderID = senderID,
                        receiverID = receiverID
                    )
                )
            }
            _loading.update { true }
            repository.askQuestion(
                prevQuestion = messages.value,
                question = question,
                senderID = senderID,
                receiverID = receiverID,
                chatID = chatID
            ).also { baseModel ->
                _loading.update { false }
                when (baseModel) {
                    is BaseModel.Success -> {
                        withContext(Dispatchers.IO) {
                            database.answerDao().addAnswer(
                                answerEntity = AnswerEntity(
                                    chatID = chatID,
                                    role = "assistant",
                                    content = baseModel.data.choices.first().message.content,
                                    senderID = senderID,
                                    receiverID = receiverID
                                )
                            )
                        }
                    }

                    is BaseModel.Error -> {
                        println("Something wrong : ${baseModel.error}")
                    }

                    else -> {}
                }
            }
        }
    }

}