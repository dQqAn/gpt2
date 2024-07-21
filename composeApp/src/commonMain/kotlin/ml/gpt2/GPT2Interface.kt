package ml.gpt2

import kotlinx.coroutines.flow.StateFlow

interface GPT2Interface {
    fun launchAutocomplete()
    fun refreshPrompt()

    val prompt: StateFlow<String>
    val completion: StateFlow<String>
}