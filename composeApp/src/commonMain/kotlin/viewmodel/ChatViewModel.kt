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
import ml.gpt2.GPT2Interface
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalResourceApi::class)
class ChatViewModel(
//    private val bertHelper: BertHelper,
//    private val bertHelper: BertQaHelper,
//    private val database: AppDatabase,
//    private val repository: Repository
) : ViewModel(), KoinComponent {
//) : ViewModel(){

    private val database: AppDatabase by inject()
    private val repository: Repository by inject()
    private val bertHelper: BertHelper by inject()
    private val gpt2Client: GPT2Interface by inject()

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

    private fun getAllTitles(): String {
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

    private suspend fun answerQuestion(question: String?, chatID: String) {
        question?.let {
            database.answerDao().addAnswer(
                answerEntity = AnswerEntity(
                    chatID = chatID,
                    role = "assistant",
                    content = it,
                    senderID = chatID,
                    receiverID = "gpt"
                )
            )
        }
    }

    init {
        viewModelScope.launch {

        }
    }

    fun askQuestion(question: String, chatID: String, senderID: String, receiverID: String) {

        if (_content.isBlank()) { //new chat
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
                    if (_titles.isEmpty()) {
                        getTitles()
                    }
                    val questionIndex = question.toIntOrNull()
                    if (questionIndex != null && 0 <= questionIndex && questionIndex <= _titles.size) {
                        database.answerDao().addAnswer(
                            answerEntity = AnswerEntity(
                                chatID = chatID,
                                role = "assistant",
                                content = getContent(questionIndex),
                                senderID = chatID,
                                receiverID = "gpt"
                            )
                        )
//                        _titles = emptyList()
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
        } else { //todo: check _content id from database
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

                //load all prevquestions from database
                for (item in bertHelper.answer(_content, question)) {
                    answerQuestion(item, chatID)
                }

                /*if (question.lowercase() == "bert") {
                    answerQuestion(question)
                } else if (question.lowercase() == "gpt") {
                    //gpt
                } else {
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
                }*/
            }
        }
    }

}