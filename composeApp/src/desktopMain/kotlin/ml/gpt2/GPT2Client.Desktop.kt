package ml.gpt2

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

actual class GPT2Client : GPT2Interface, ViewModel() {
    override fun launchAutocomplete() {
        TODO("Not yet implemented")
    }

    override fun refreshPrompt() {
        TODO("Not yet implemented")
    }

    override val prompt: StateFlow<String>
        get() = TODO("Not yet implemented")
    override val completion: StateFlow<String>
        get() = TODO("Not yet implemented")

}