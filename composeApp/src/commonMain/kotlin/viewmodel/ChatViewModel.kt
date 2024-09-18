import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import ml.image_classification.ImageClassifierHelper
import ml.image_classification.ImageClassifierHelperInterface
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import presentation.components.SpeechInterface
import repositories.FirebaseMessageRepository
import util.GetCurrentDate
import java.io.File

class ChatViewModel : ViewModel(), KoinComponent,
    ImageClassifierHelper.ClassifierListener {
    private val database: AnswerDatabase by inject()
    private val messageRepository: MessageRepository by inject()
    private val bertHelper: BertHelper by inject()

    private val speech: SpeechInterface by inject()

    private val imageClassifier: ImageClassifierHelperInterface by inject {
        parametersOf(this as ImageClassifierHelper.ClassifierListener)
    }

    private val _messageText = speech.messageText
    val messageText = _messageText.asStateFlow()
    fun changeMessageText(text: String) {
        _messageText.update {
            text
        }
    }

    private val _isListening = mutableStateOf(false)

    /*val isListening = _isListening
    fun changeIsListening() {
        _isListening.value = !_isListening.value
    }*/

    fun startSpeechToText() {
        speech.startSpeechToText(_isListening)
    }

    fun stopSpeechToText() {
        speech.stopSpeechToText(_isListening)
    }

    private val firebaseMessageRepository: FirebaseMessageRepository by inject()

    private val _chatID = firebaseMessageRepository.chatID
    val chatID = _chatID.asStateFlow()
    fun changeChatID(chatID: String) {
        _chatID.update {
            chatID
        }
    }

    private val _isNewChat = firebaseMessageRepository.isNewChat
    val isNewChat = _isNewChat.asStateFlow()
    fun changeIsNewChat(isNewChat: Boolean) {
        _isNewChat.update {
            isNewChat
        }
    }

    val selectedImages: MutableState<List<File?>> = mutableStateOf(listOf())

    @Composable
    fun launchGallery(openGallery: MutableState<Boolean>, showRationalDialog: MutableState<Boolean>) {
        firebaseMessageRepository.launchGallery(openGallery, showRationalDialog, selectedImages)
    }

    fun uploadFiles(contentType: String, chatID: String, senderID: String, receiverID: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseMessageRepository.uploadFiles(
                    selectedImages,
                    contentType,
                    chatID,
                    senderID,
                    receiverID,
                    viewModelScope,
                    getLastMessageID() + 1
                )
            }
        }
    }

    fun getFile(path: String, selectedByteArrayImages: MutableState<ByteArray?>) {
        firebaseMessageRepository.getOnlineFile(path, selectedByteArrayImages)
    }

    private val _localMessageList: MutableStateFlow<List<AnswerEntity?>> = MutableStateFlow(emptyList())
    val localMessageList: StateFlow<List<AnswerEntity?>> = _localMessageList.asStateFlow()

    private val _remoteMessageList = firebaseMessageRepository.messageList
    val remoteMessageList = _remoteMessageList.asStateFlow()

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

    init {
        changeMessageText("")

        _remoteMessageList.update {
            listOf()
        }
        _localMessageList.update {
            listOf()
        }

        loadMessages(_chatID.value, senderID.value, receiverID.value, _isNewChat.value)
    }

    val currentUserID = firebaseMessageRepository.currentUserID
    val currentUserMail = firebaseMessageRepository.currentUserMail
    val friendID: StateFlow<String?> = firebaseMessageRepository.friendID

    val isTitledLoaded = mutableStateOf(false)

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private fun getAllTitles(): String {
        var allTitles = ""
        for ((index, message) in getTitles().withIndex()) {
            allTitles += "$index. $message\n"
        }
        return allTitles
    }

    private fun getLastMessageID(): Int {
        return if (_remoteMessageList.value.isEmpty()) {
            0
        } else {
            _remoteMessageList.value.last()!!.id
        }
    }

    private fun loadMessages(chatID: String?, senderID: String?, receiverID: String?, isNewChat: Boolean) {
        if (!chatID.isNullOrBlank()) {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    messageRepository.getMessages(chatID).collect { data ->
                        if (data.isNotEmpty()) {
                            _localMessageList.update { data }
                        }
                    }
                }
            }
            if (isNewChat && senderID != null && receiverID != null) {
                if (!isTitledLoaded.value) { //for gpt chat
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            database.answerDao().addAnswer(
                                answerEntity = AnswerEntity(
                                    chatID = chatID,
                                    role = "assistant",
                                    contentType = contentTypeMessage,
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
                    contentType = contentTypeMessage,
                    content = it,
                    senderID = senderID,
                    receiverID = receiverID,
                    date = GetCurrentDate()
                )
            )
        }
    }

    fun localAddAnswer(answerEntity: AnswerEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.answerDao().addAnswer(answerEntity)
            }
        }
    }

    fun addAnswer(content: String, contentType: String, chatID: String, senderID: String, receiverID: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseMessageRepository.addAnswer(
                    content,
                    contentType,
                    chatID,
                    senderID,
                    receiverID,
                    getLastMessageID() + 1
                )
            }
        }
    }

    fun getAnswer(chatID: String, senderID: String, receiverID: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                firebaseMessageRepository.getAnswer(chatID, senderID, receiverID)
            }
        }
    }

    fun newChatAiQuestion(question: String, contentType: String, chatID: String, senderID: String, receiverID: String) {
        if (_content.isBlank()) { //new chat
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    database.answerDao().addAnswer(
                        answerEntity = AnswerEntity(
                            chatID = chatID,
                            role = "user",
                            contentType = contentType,
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
                                contentType = contentType,
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
                                contentType = contentType,
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
                            contentType = contentType,
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

    fun createBitmapFromFileByteArray(byteArray: ByteArray?): Bitmap? {
        return if (byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } else {
            null
        }
        /*val file = File(filepath)
        return if (file.exists()) {
            val imageBytes = Base64.getDecoder().decode(filepath)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            return bitmap
        } else {
            null
        }*/
    }

    fun createBitmapFromFilePath(filepath: String): Bitmap? {
        val file = File(filepath)
        return if (file.exists()) {
            return BitmapFactory.decodeFile(filepath)
        } else {
            null
        }
    }

    fun imageClassify(image: Bitmap, text: MutableState<String?>) {
        imageClassifier.classify(image, text)
    }

    override fun onClassifierError(error: String) {
        imageClassifier.clearImageClassifier()
        println(error)
    }

    override fun onClassifierResults(
        text: MutableState<String?>,
        results: List<List<ImageClassifierHelper.MyImageCategory>>?,
        inferenceTime: Long
    ) {
        results?.let {
            text.value = ""
            for (result in it) {
                for (category in result) {
                    text.value += category.getLabel() + " "
                }
            }
        }
    }
}