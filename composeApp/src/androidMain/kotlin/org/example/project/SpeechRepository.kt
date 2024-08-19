import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.MutableState
import androidx.core.bundle.Bundle
import presentation.components.SpeechInterface
import java.util.*

actual class SpeechRepository(
    val messageText: MutableState<String>,
    private val context: Context
) : SpeechInterface {

    //    private lateinit var textToSpeech: TextToSpeech

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    init {
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something")

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(v: Float) {}

            override fun onBufferReceived(bytes: ByteArray) {}

            override fun onEndOfSpeech() {}

            override fun onError(i: Int) {}

            override fun onResults(bundle: Bundle) {
                val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)//getting all the matches
                //displaying the first match
                if (matches != null) {
                    messageText.value = matches[0]
//                    println("Speech result: " + matches[0])
                }
            }

            override fun onPartialResults(bundle: Bundle) {}

            override fun onEvent(i: Int, bundle: Bundle) {}
        })

        /*textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Toast.makeText(context, "language is not supported", Toast.LENGTH_LONG).show()
                }
            }
        }*/
    }

    override fun startSpeechToText(isListening: MutableState<Boolean>) {
        println("Mic listening...")
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    override fun stopSpeechToText(isListening: MutableState<Boolean>) {
        println("Mic is not listening.")
        speechRecognizer.stopListening()
    }

    override fun textToSpeech(text: String) {
//        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}