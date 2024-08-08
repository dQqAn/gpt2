import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ml.bert.BertHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.FirebaseMessageRepository
import util.GetCurrentDate

class ChatViewModel() : ViewModel(), KoinComponent {
    private val database: AnswerDatabase by inject()
    private val messageRepository: MessageRepository by inject()
    private val bertHelper: BertHelper by inject()

    private val firebaseMessageRepository: FirebaseMessageRepository by inject<FirebaseMessageRepository>()

    //    private val _messageList: MutableStateFlow<List<AnswerEntity?>> = MutableStateFlow(emptyList())
    //    val messageList: StateFlow<List<AnswerEntity?>> = _messageList.asStateFlow()
    val _remoteMessageList = firebaseMessageRepository.messageList
    val remoteMessageList = _remoteMessageList.asStateFlow()

    val currentUserID = firebaseMessageRepository.currentUserID
    val currentUserMail = firebaseMessageRepository.currentUserMail
    val friendID: StateFlow<String?> = firebaseMessageRepository.friendID

    val isTitledLoaded = mutableStateOf(false)

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _receiverID = mutableStateOf("")
    val receiverID = _receiverID
    fun changeReceiverID(senderID: String) {
        _receiverID.value = senderID
    }

    private val _senderID = mutableStateOf("")
    val senderID = _senderID
    fun changeSenderID(senderID: String) {
        _senderID.value = senderID
    }

    private fun getAllTitles(): String {
        var allTitles = ""
        for ((index, message) in getTitles().withIndex()) {
            allTitles += "$index. $message\n"
        }
        return allTitles
    }

    fun loadMessages(chatID: String?, senderID: String, receiverID: String, isNewChat: Boolean) {
        chatID?.let {
            /*viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    messageRepository.getMessages(it).collect { data ->
                        if (data.isNotEmpty()) {
                            _remoteMessageList.update { data }
                        }
                    }
                }
            }*/
            if (isNewChat && !isTitledLoaded.value) { //for gpt chat
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        database.answerDao().addAnswer(
                            answerEntity = AnswerEntity(
                                chatID = it,
                                role = "assistant",
                                content = getAllTitles(),
                                senderID = senderID,
                                receiverID = receiverID,
                                date = GetCurrentDate()
                            )
                        )
                    }
                    isTitledLoaded.value = true
                }
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

    private suspend fun answerQuestion(question: String?, senderID: String, receiverID: String, chatID: String) {
        question?.let {
            database.answerDao().addAnswer(
                answerEntity = AnswerEntity(
                    chatID = chatID,
                    role = "assistant",
                    content = it,
                    senderID = senderID,
                    receiverID = receiverID,
                    date = GetCurrentDate()
                )
            )
        }
    }

    fun addAnswer(message: String, chatID: String, senderID: String, receiverID: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseMessageRepository.addAnswer(message, chatID, senderID, receiverID)
            }
        }
    }

    fun getAnswer(chatID: String, senderID: String, receiverID: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseMessageRepository.getAnswer(chatID, senderID, receiverID)
            }
        }
    }

    fun newChatAiQuestion(question: String, chatID: String, senderID: String, receiverID: String) {
        if (_content.isBlank()) { //new chat
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    database.answerDao().addAnswer(
                        answerEntity = AnswerEntity(
                            chatID = chatID,
                            role = "user",
                            content = question,
                            senderID = senderID,
                            receiverID = receiverID,
                            date = GetCurrentDate()
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
                                senderID = senderID,
                                receiverID = receiverID,
                                date = GetCurrentDate()
                            )
                        )
                    } else {
                        database.answerDao().addAnswer(
                            answerEntity = AnswerEntity(
                                chatID = chatID,
                                role = "assistant",
                                content = getAllTitles(),
                                senderID = senderID,
                                receiverID = receiverID,
                                date = GetCurrentDate()
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
                            receiverID = receiverID,
                            date = GetCurrentDate()
                        )
                    )
                }
                _loading.update { true }

                //load all prevquestions from database
                for (item in bertHelper.answer(_content, question)) {
                    answerQuestion(item, senderID, receiverID, chatID)
                }

                /*repository.askQuestion(
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
                    }*/
            }
        }
    }

}