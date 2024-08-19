package presentation.components

import androidx.compose.runtime.MutableState

interface SpeechInterface {
    fun startSpeechToText(isListening: MutableState<Boolean>)
    fun stopSpeechToText(isListening: MutableState<Boolean>)
    fun textToSpeech(text: String)
}