package ml.image_classification

expect class ImageClassifierHelper : ImageClassifierHelperInterface {
    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Any>?, // results: List<Classifications>?,
            inferenceTime: Long
        )
    }
}