package ml.bert

import android.content.Context
import android.os.SystemClock
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer.BertQuestionAnswererOptions
import org.tensorflow.lite.task.text.qa.QaAnswer

actual class BertQaHelper(
//    private val context: Context,
//    private val numThreads: Int = 2,
//    private val currentDelegate: Int = 0,
//    private val answererListener: AnswererListener?
) /*: BertHelper, KoinComponent {*/
//) : BertHelper {
    : BertHelper, KoinComponent {
    //    : BertHelper {
    private val numThreads: Int = 2
    private val currentDelegate: Int = 0

    private val context: Context by inject()

    private var bertQuestionAnswerer: BertQuestionAnswerer? = null

    init {
        setupBertQuestionAnswerer()
    }

    override fun clearBertQuestionAnswerer() {
        bertQuestionAnswerer = null
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun setupBertQuestionAnswerer() {
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default
            }

            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
//                    answererListener?.onError("GPU is not supported on this device")
                    println("GPU is not supported on this device")
                }
            }

            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        val options = BertQuestionAnswererOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .build()

        try {
            bertQuestionAnswerer = BertQuestionAnswerer.createFromFileAndOptions(context, BERT_QA_MODEL, options)
        } catch (e: IllegalStateException) {
//            answererListener?.onError("Bert Question Answerer failed to initialize. See error logs for details")
//            Log.e(TAG, "TFLite failed to load model with error: " + e.message)
            println("TFLite failed to load model with error: " + e.message)
        }
    }

    override fun answer(contextOfQuestion: String, question: String) {
        println("ANSWER")
        println("answer: " + bertQuestionAnswerer)
        if (bertQuestionAnswerer == null) {
            setupBertQuestionAnswerer()
        }

        // Inference time is the difference between the system time at the start and finish of the
        // process
        var inferenceTime = SystemClock.uptimeMillis()

        val answers = bertQuestionAnswerer?.answer(contextOfQuestion, question)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
//        answererListener?.onResults(answers, inferenceTime)
        println(answers)
    }

    override fun dump() {
        println("dump")
    }

    interface AnswererListener {
        fun onError(error: String)
        fun onResults(
            results: List<QaAnswer>?,
            inferenceTime: Long
        )
    }

    override val BERT_QA_MODEL = "mobilebert.tflite"
    override val TAG = "BertQaHelper"
    override val DELEGATE_CPU = 0
    override val DELEGATE_GPU = 1
    override val DELEGATE_NNAPI = 2
}