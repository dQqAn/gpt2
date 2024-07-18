package ml.bert

actual class BertQaHelper(
    private val numThreads: Int = 2,
    private val currentDelegate: Int = 0,
) {
    actual fun clearBertQuestionAnswerer() {
    }

    actual fun answer(contextOfQuestion: String, question: String) {
    }

    actual val BERT_QA_MODEL = "mobilebert.tflite"
    actual val TAG = "BertQaHelper"
    actual val DELEGATE_CPU = 0
    actual val DELEGATE_GPU = 1
    actual val DELEGATE_NNAPI = 2
}