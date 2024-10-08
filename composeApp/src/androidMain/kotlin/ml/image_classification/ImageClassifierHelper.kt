package ml.image_classification

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import androidx.compose.runtime.MutableState
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

actual class ImageClassifierHelper(
    private var threshold: Float = 0.5f,
    private var numThreads: Int = 2,
    private var maxResults: Int = 3,
    private var currentDelegate: Int = 0,
    private var currentModel: Int = 0,
    private val context: Context,
    private val imageClassifierListener: ClassifierListener?
) : ImageClassifierHelperInterface {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    override fun clearImageClassifier() {
        imageClassifier = null
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default
            }

            DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    baseOptionsBuilder.useGpu()
                } else {
                    imageClassifierListener?.onClassifierError("GPU is not supported on this device")
                }
            }

            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        val modelName =
            when (currentModel) {
                MODEL_MOBILENETV1 -> "mobilenetv1.tflite"
                MODEL_EFFICIENTNETV0 -> "efficientnet-lite0.tflite"
                MODEL_EFFICIENTNETV1 -> "efficientnet-lite1.tflite"
                MODEL_EFFICIENTNETV2 -> "efficientnet-lite2.tflite"
                else -> "mobilenetv1.tflite"
            }

        try {
            imageClassifier =
                ImageClassifier.createFromFileAndOptions(context, modelName, optionsBuilder.build())
        } catch (e: IllegalStateException) {
            imageClassifierListener?.onClassifierError(
                "Image classifier failed to initialize. See error logs for details"
            )
            Log.e(TAG, "TFLite failed to load model with error: " + e.message)
        }
    }

    override fun classify(image: Bitmap, text: MutableState<String?>, rotation: Int) {
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        // Inference time is the difference between the system time at the start and finish of the
        // process
        var inferenceTime = SystemClock.uptimeMillis()

        // Create preprocessor for the image.
        // See https://www.tensorflow.org/lite/inference_with_metadata/
        //            lite_support#imageprocessor_architecture
        val imageProcessor =
            ImageProcessor.Builder()
                .build()

        // Preprocess the image and convert it into a TensorImage for classification.
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        val results = imageClassifier?.classify(tensorImage, imageProcessingOptions)

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        imageClassifierListener?.onClassifierResults(
            text,
            results?.map {
                it.categories.map {
                    MyImageCategory(
                        label = it.label,
                        displayName = it.displayName,
                        score = it.score,
                        index = it.index,
                    )
                }
            },
            inferenceTime
        )
    }

    // Receive the device rotation (Surface.x values range from 0->3) and return EXIF orientation
    // http://jpegclub.org/exif_orientation.html
    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        when (rotation) {
            Surface.ROTATION_270 ->
                return ImageProcessingOptions.Orientation.BOTTOM_RIGHT

            Surface.ROTATION_180 ->
                return ImageProcessingOptions.Orientation.RIGHT_BOTTOM

            Surface.ROTATION_90 ->
                return ImageProcessingOptions.Orientation.TOP_LEFT

            else ->
                return ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

    actual interface ClassifierListener {
        actual fun onClassifierError(error: String)
        actual fun onClassifierResults(
            text: MutableState<String?>,
            results: List<List<MyImageCategory>>?,
            inferenceTime: Long
        )
    }

    /*actual class MyImageClassifier actual constructor(
        list: List<MyImageCategory>,
        headIndex: Int
    ) {
        actual fun getCategories(): List<MyImageCategory> {
            return this.getCategories()
        }

        actual fun getHeadIndex(): Int {
            return this.getHeadIndex()
        }
    }*/

    actual class MyImageCategory actual constructor(
        private val label: String,
        private val displayName: String,
        private val score: Float,
        private val index: Int
    ) {
        actual fun getLabel(): String {
            return this.label
        }

        actual fun getDisplayName(): String {
            return this.displayName
        }

        actual fun getScore(): Float {
            return this.score
        }

        actual fun getIndex(): Int {
            return this.index
        }
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
        const val MODEL_MOBILENETV1 = 0
        const val MODEL_EFFICIENTNETV0 = 1
        const val MODEL_EFFICIENTNETV1 = 2
        const val MODEL_EFFICIENTNETV2 = 3

        private const val TAG = "ImageClassifierHelper"
    }
}