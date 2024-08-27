package ml.image_classification

import androidx.compose.runtime.MutableState

expect class ImageClassifierHelper : ImageClassifierHelperInterface {
    interface ClassifierListener {
        fun onClassifierError(error: String)
        fun onClassifierResults(
            text: MutableState<String?>,
            results: List<List<ImageClassifierHelper.MyImageCategory>>?,
            inferenceTime: Long
        )
    }

    /*class MyImageClassifier(list: List<MyImageCategory>, headIndex: Int) {
       fun getCategories(): List<MyImageCategory>
       fun getHeadIndex(): Int
   }*/

    class MyImageCategory(label: String, displayName: String, score: Float, index: Int) {
        fun getLabel(): String
        fun getDisplayName(): String
        fun getScore(): Float
        fun getIndex(): Int
    }
}