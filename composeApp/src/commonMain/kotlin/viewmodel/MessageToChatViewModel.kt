package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class MessageToChatViewModel : ViewModel(), KoinComponent {
    private val _chatID = mutableStateOf<String?>(null)
    val chatID = _chatID

    fun changeChatID(chatID: String?) {
        viewModelScope.launch {
            _chatID.value = chatID
        }
    }

    init {

    }
}