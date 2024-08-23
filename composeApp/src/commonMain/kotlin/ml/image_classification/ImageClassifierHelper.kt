package ml.image_classification

expect class ImageClassifierHelper : ImageClassifierHelperInterface {
    interface ClassifierListener {
        fun onClassifierError(error: String)
        fun onClassifierResults(
            results: List<Any>?, // results: List<Classifications>?,
            inferenceTime: Long
        )
    }
}