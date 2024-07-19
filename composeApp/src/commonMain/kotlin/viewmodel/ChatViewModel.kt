import androidx.compose.runtime.MutableState
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

//    private val _chatID = mutableStateOf<String?>(null)
//    val chatID = _chatID
    /*fun changeChatID(chatID: String?) {
        viewModelScope.launch {
            _chatID.value = chatID
        }
    }*/

    fun getAllTitles(): String {
        var allTitles = ""
        for ((index, message) in getTitles().withIndex()) {
            allTitles += "$index. $message\n"
        }
        return allTitles
    }

    fun loadMessages(chatID: String, isNewChat: MutableState<Boolean>) {
        viewModelScope.launch {
            repository.getMessages(chatID).collect { data ->
                _messages.update { data }
            }
        }
        viewModelScope.launch {
            if (isNewChat.value) {
                withContext(Dispatchers.IO) {
                    database.answerDao().addAnswer(
                        answerEntity = AnswerEntity(
                            chatID = chatID,
                            role = "assistant",
                            content = getAllTitles(),
                            senderID = chatID,
                            receiverID = "gpt"
                        )
                    )
                }
                isNewChat.value = false
            }
        }
    }

    private var _titles: List<String> = emptyList()
    private val client = LoadDataSetClient()
    private fun getTitles(): List<String?> {
        //        val rnds = (0..10).random()
        //    var questions: List<String> = emptyList()

        if (_titles.isEmpty()) {
            client.loadJson()?.let {
                _titles = it.getTitles()
//                questions = it.questions
            }
        }
        return _titles
    }

    private var _content: String = ""
    private fun getContent(index: Int): String {
        client.loadJson()?.let {
            _content = it.getContents()[index]
        }
        return _content
    }

    init {
        viewModelScope.launch {

        }
    }

    fun askQuestion(question: String, chatID: String, senderID: String, receiverID: String) {

        if (_titles.isNotEmpty()) {
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
                    val questionIndex = question.toInt()
                    if (0 < questionIndex && questionIndex <= _titles.size) {
                        database.answerDao().addAnswer(
                            answerEntity = AnswerEntity(
                                chatID = chatID,
                                role = "assistant",
                                content = getContent(questionIndex),
                                senderID = chatID,
                                receiverID = "gpt"
                            )
                        )
                        _titles = emptyList()
                    } else {
                        database.answerDao().addAnswer(
                            answerEntity = AnswerEntity(
                                chatID = chatID,
                                role = "assistant",
                                content = getAllTitles(),
                                senderID = chatID,
                                receiverID = "gpt"
                            )
                        )
                    }
                }
            }
        } else {
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

}