import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MessageViewModel : ViewModel(), KoinComponent {

    private val repository: Repository by inject()

    private val _messages: MutableStateFlow<List<String?>> = MutableStateFlow(emptyList())
    val messages = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            repository.deleteTable()
            repository.getChats().collect { data ->
                _messages.update { data }
            }
        }
    }
}