package ml.bert

interface BertHelper {
    fun clearBertQuestionAnswerer()
    fun answer(contextOfQuestion: String, question: String)

    val BERT_QA_MODEL: String
    val TAG: String
    val DELEGATE_CPU: Int
    val DELEGATE_GPU: Int
    val DELEGATE_NNAPI: Int
}