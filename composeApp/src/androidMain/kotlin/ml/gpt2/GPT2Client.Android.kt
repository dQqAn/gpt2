package ml.gpt2

import GPT2Tokenizer
import android.app.Application
import android.util.JsonReader
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.channels.FileChannel
import kotlin.math.exp
import kotlin.random.Random

private const val SEQUENCE_LENGTH = 64
private const val VOCAB_SIZE = 50257
private const val NUM_HEAD = 12
private const val NUM_LITE_THREADS = 4
private const val MODEL_PATH = "model.tflite"
private const val VOCAB_PATH = "gpt2-vocab.json"
private const val MERGES_PATH = "gpt2-merges.txt"
private const val TAG = "GPT2Client"

private typealias Predictions = Array<Array<FloatArray>>

enum class GPT2StrategyEnum { GREEDY, TOPK }
data class GPT2Strategy(val strategy: GPT2StrategyEnum, val value: Int = 0)

actual class GPT2Client : GPT2Interface, ViewModel(), KoinComponent {
    private val application: Application by inject()
    private val initJob: Job
    private var autocompleteJob: Job? = null
    private lateinit var tokenizer: GPT2Tokenizer
    private lateinit var tflite: Interpreter

    private val prompts = arrayOf(
        "Before boarding your rocket to Mars, remember to pack these items",
        "In a shocking finding, scientist discovered a herd of unicorns living in a remote, previously unexplored valley, in the Andes Mountains. Even more surprising to the researchers was the fact that the unicorns spoke perfect English.",
        "Legolas and Gimli advanced on the orcs, raising their weapons with a harrowing war cry.",
        "Today, scientists confirmed the worst possible outcome: the massive asteroid will collide with Earth",
        "Hugging Face is a company that releases awesome projects in machine learning because"
    )

    private val _prompt = MutableStateFlow(prompts.random())
    override val prompt = _prompt.asStateFlow()

    private val _completion = MutableStateFlow("")
    override val completion = _completion.asStateFlow()

    private var strategy = GPT2Strategy(GPT2StrategyEnum.TOPK, 40)

    init {
        initJob = viewModelScope.launch {
            val encoder = loadEncoder()
            val decoder = encoder.entries.associateBy({ it.value }, { it.key })
            val bpeRanks = loadBpeRanks()

            tokenizer = GPT2Tokenizer(encoder, decoder, bpeRanks)
            tflite = loadModel()
//            println("Start...")
        }
    }

    /*override fun onCleared() {
        super.onCleared()
        tflite.close()
    }*/

    override fun launchAutocomplete() {
        autocompleteJob = viewModelScope.launch {
            initJob.join()
            autocompleteJob?.cancelAndJoin()
            _completion.value = ""
//            println("prompt: "+_prompt.value)
            generate(_prompt.value)
        }
    }

    override fun refreshPrompt() {
        _prompt.value = prompts.random()
        launchAutocomplete()
    }

    private suspend fun generate(text: String, nbTokens: Int = 100) = withContext(Dispatchers.Default) {
        val tokens = tokenizer.encode(text)
        repeat(nbTokens) {
            val maxTokens = tokens.takeLast(SEQUENCE_LENGTH).toIntArray()
            val paddedTokens = maxTokens + IntArray(SEQUENCE_LENGTH - maxTokens.size)
            val inputIds = Array(1) { paddedTokens }

            val predictions: Predictions = Array(1) { Array(SEQUENCE_LENGTH) { FloatArray(VOCAB_SIZE) } }
            val outputs = mutableMapOf<Int, Any>(0 to predictions)

            tflite.runForMultipleInputsOutputs(arrayOf(inputIds), outputs)
            val outputLogits = predictions[0][maxTokens.size - 1]

            val nextToken: Int = when (strategy.strategy) {
                GPT2StrategyEnum.TOPK -> {
                    val filteredLogitsWithIndexes = outputLogits
                        .mapIndexed { index, fl -> (index to fl) }
                        .sortedByDescending { it.second }
                        .take(strategy.value)

                    // Softmax computation on filtered logits
                    val filteredLogits = filteredLogitsWithIndexes.map { it.second }
                    val maxLogitValue = filteredLogits.maxOrNull()!!
                    val logitsExp = filteredLogits.map { exp(it - maxLogitValue) }
                    val sumExp = logitsExp.sum()
                    val probs = logitsExp.map { it.div(sumExp) }

                    val logitsIndexes = filteredLogitsWithIndexes.map { it.first }
                    sample(logitsIndexes, probs)
                }

                else -> outputLogits.argmax()
            }

            tokens.add(nextToken)
            val decodedToken = tokenizer.decode(listOf(nextToken))
//            _completion.postValue(_completion.value + decodedToken)
            println("decodedToken: " + decodedToken)
            _completion.update {
                _completion.value + decodedToken
            }
            yield()
        }
    }

    private suspend fun loadModel(): Interpreter = withContext(Dispatchers.IO) {
        val assetFileDescriptor = application.assets.openFd(MODEL_PATH)
        assetFileDescriptor.use {
            val fileChannel = FileInputStream(assetFileDescriptor.fileDescriptor).channel
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, it.startOffset, it.declaredLength)

            val opts = Interpreter.Options()
            opts.setNumThreads(NUM_LITE_THREADS)
            return@use Interpreter(modelBuffer, opts)
        }
    }

    private suspend fun loadEncoder(): Map<String, Int> = withContext(Dispatchers.IO) {
        hashMapOf<String, Int>().apply {
            val vocabStream = application.assets.open(VOCAB_PATH)
            vocabStream.use {
                val vocabReader = JsonReader(InputStreamReader(it, "UTF-8"))
                vocabReader.beginObject()
                while (vocabReader.hasNext()) {
                    val key = vocabReader.nextName()
                    val value = vocabReader.nextInt()
                    put(key, value)
                }
                vocabReader.close()
            }
            vocabStream.close()
        }
    }

    private suspend fun loadBpeRanks(): Map<Pair<String, String>, Int> = withContext(Dispatchers.IO) {
        hashMapOf<Pair<String, String>, Int>().apply {
            val mergesStream = application.assets.open(MERGES_PATH)
            mergesStream.use { stream ->
                val mergesReader = BufferedReader(InputStreamReader(stream))
                mergesReader.useLines { seq ->
                    seq.drop(1).forEachIndexed { i, s ->
                        val list = s.split(" ")
                        val keyTuple = list[0] to list[1]
                        put(keyTuple, i)
                    }
                }
            }
            mergesStream.close()
        }
    }
}

private fun randomIndex(probs: List<Float>): Int {
    val rnd = probs.sum() * Random.nextFloat()
    var acc = 0f

    probs.forEachIndexed { i, fl ->
        acc += fl
        if (rnd < acc) {
            return i
        }
    }

    return probs.size - 1
}

private fun sample(indexes: List<Int>, probs: List<Float>): Int {
    val i = randomIndex(probs)
    return indexes[i]
}

private fun FloatArray.argmax(): Int {
    var bestIndex = 0
    repeat(size) {
        if (this[it] > this[bestIndex]) {
            bestIndex = it
        }
    }

    return bestIndex
}

/*@BindingAdapter("prompt", "completion")
fun TextView.formatCompletion(prompt: String, completion: String): Unit {
    text = when {
        completion.isEmpty() -> prompt
        else -> {
            val str = SpannableStringBuilder(prompt + completion)
            val bgCompletionColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, context.theme)
            str.setSpan(android.text.style.BackgroundColorSpan(bgCompletionColor), prompt.length, str.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            str
        }
    }
}*/
