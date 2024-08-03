import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.FirebaseMessageRepository

class MessageViewModel : ViewModel(), KoinComponent {

    private val messageRepository: MessageRepository by inject()
    private val firebaseMessageRepository: FirebaseMessageRepository by inject()

    val currentUserID = firebaseMessageRepository.currentUserID

    private val _messages: MutableStateFlow<List<String?>> = MutableStateFlow(emptyList())
    val messages = _messages.asStateFlow()

    init {
        viewModelScope.launch {
//            repository.deleteTable()
            messageRepository.getChats().collect { data ->
                _messages.update { data }
            }
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                messageRepository.deleteChat(chatID = chatId)
            }
        }
    }
}