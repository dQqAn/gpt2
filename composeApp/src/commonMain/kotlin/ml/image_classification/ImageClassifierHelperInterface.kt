package ml.image_classification

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState

interface ImageClassifierHelperInterface {
    fun classify(image: Bitmap, text: MutableState<String?>, rotation: Int = 90)
    fun clearImageClassifier()
}