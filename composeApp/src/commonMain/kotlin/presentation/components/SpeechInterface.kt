package presentation.components

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow

interface SpeechInterface {
    fun startSpeechToText(isListening: MutableState<Boolean>)
    fun stopSpeechToText(isListening: MutableState<Boolean>)
    fun textToSpeech(text: String)
    val messageText: MutableStateFlow<String?>
}