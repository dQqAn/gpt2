package ml.bert

actual class BertQaHelper(
    override val BERT_QA_MODEL: String,
    override val TAG: String,
    override val DELEGATE_CPU: Int,
    override val DELEGATE_GPU: Int,
    override val DELEGATE_NNAPI: Int
//    private val numThreads: Int = 2,
//    private val currentDelegate: Int = 0,
//) : BertHelper {
) : BertHelper {
    /*override fun clearBertQuestionAnswerer() {
    }

    override fun answer(contextOfQuestion: String, question: String) {
    }

    override val BERT_QA_MODEL = "mobilebert.tflite"
    override val TAG = "BertQaHelper"
    override val DELEGATE_CPU = 0
    override val DELEGATE_GPU = 1
    override val DELEGATE_NNAPI = 2*/

    override fun clearBertQuestionAnswerer() {
        TODO("Not yet implemented")
    }

    override fun answer(contextOfQuestion: String, question: String): List<String?> {
        TODO("Not yet implemented")
    }

}