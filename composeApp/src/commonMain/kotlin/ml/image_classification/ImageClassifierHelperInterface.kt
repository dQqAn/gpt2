package ml.image_classification

import android.graphics.Bitmap

interface ImageClassifierHelperInterface {
    fun classify(image: Bitmap, rotation: Int = 90)
    fun clearImageClassifier()
}