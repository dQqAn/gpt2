import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.FirebaseMessageRepository

class MessageViewModel : ViewModel(), KoinComponent {
    private val firebaseMessageRepository: FirebaseMessageRepository by inject()

    private val messageRepository: MessageRepository by inject()

    val currentUserMail = firebaseMessageRepository.currentUserMail

    val currentUserID = firebaseMessageRepository.currentUserID
    fun otherUserID(mail: String?) {
        firebaseMessageRepository.otherUserID(mail)
    }

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

    private val _chats: MutableStateFlow<List<String?>> = MutableStateFlow(emptyList())
    val chats = _chats.asStateFlow()

    //first state whether the search is happening or not
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    fun changeIsSearching() {
        _isSearching.value = !_isSearching.value
    }

    //second state the text typed by the user
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    fun changeSearchText(text: String) {
        _searchText.value = text
        otherUserID(text)
    }

    private val _searchedList = MutableStateFlow(emptyList<String>())
    val searchedList = searchText
        .combine(_searchedList) { text, mails ->//combine searchText with _contriesList
            if (text.isBlank()) { //return the entery list of countries if not is typed
                firebaseMessageRepository.filteredList.value
            }
            firebaseMessageRepository.filteredList.value.filter { country ->// filter and return a list of countries based on the text the user typed
                country?.uppercase()?.contains(text.trim().uppercase()) == true
            }
        }.stateIn(//basically convert the Flow returned from combine operator to StateFlow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),//it will allow the StateFlow survive 5 seconds before it been canceled
            initialValue = _searchedList.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        if (text.isNotBlank() && text.isNotEmpty()) {
            firebaseMessageRepository.getMailtoFirestore(text)
        }
    }

    fun onToogleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }

    init {
        viewModelScope.launch {
//            repository.deleteTable()
            messageRepository.getChats().collect { data ->
                _chats.update { data }
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

    @Composable
    fun takePermission(openGallery: MutableState<Boolean>, showRationalDialog: MutableState<Boolean>) {
        firebaseMessageRepository.takePermission(openGallery, showRationalDialog)
    }
}