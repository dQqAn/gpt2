package ml.whisper

import android.app.Application
import android.content.res.AssetManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executors

private const val LOG_TAG = "LibWhisper"

actual class WhisperContext(val ptr: MutableStateFlow<Long>, private val application: Application) :
    WhisperContextInterface,
    KoinComponent {
    // Meet Whisper C++ constraint: Don't access from more than one thread at a time.
    private val scope: CoroutineScope = CoroutineScope(
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    )

    override val getFilesDir: File = application.filesDir
    override val models: Array<String?>? = application.assets.list("models/")
    var mediaPlayer: MediaPlayer? = null

    override suspend fun stopPlayback() = withContext(Dispatchers.Main) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override suspend fun startPlayback(file: File) = withContext(Dispatchers.Main) {
        mediaPlayer = MediaPlayer.create(application, file.absolutePath.toUri())
        mediaPlayer?.start()
    }

    override suspend fun transcribeData(data: FloatArray, printTimestamp: Boolean): String =
        withContext(scope.coroutineContext) {
            require(ptr.value != 0L)
            val numThreads = WhisperCpuConfig.preferredThreadCount
            Log.d(LOG_TAG, "Selecting $numThreads threads")
            WhisperLib.fullTranscribe(ptr.value, numThreads, data)
            val textCount = WhisperLib.getTextSegmentCount(ptr.value)
            return@withContext buildString {
                for (i in 0 until textCount) {
                    if (printTimestamp) {
                        val textTimestamp = "[${
                            toTimestamp(
                                WhisperLib.getTextSegmentT0(
                                    ptr.value,
                                    i
                                )
                            )
                        } --> ${toTimestamp(WhisperLib.getTextSegmentT1(ptr.value, i))}]"
                        val textSegment = WhisperLib.getTextSegment(ptr.value, i)
                        append("$textTimestamp: $textSegment\n")
                    } else {
                        append(WhisperLib.getTextSegment(ptr.value, i))
                    }
                }
            }
        }

    override suspend fun benchMemory(nthreads: Int): String = withContext(scope.coroutineContext) {
        return@withContext WhisperLib.benchMemcpy(nthreads)
    }

    override suspend fun benchGgmlMulMat(nthreads: Int): String = withContext(scope.coroutineContext) {
        return@withContext WhisperLib.benchGgmlMulMat(nthreads)
    }

    override suspend fun release() = withContext(scope.coroutineContext) {
        if (ptr.value != 0L) {
            WhisperLib.freeContext(ptr.value)
            ptr.update {
                0
            }
        }
    }

    protected fun finalize() {
        runBlocking {
            release()
        }
    }

    override suspend fun copyData(
        assetDirName: String,
        destDir: File,
        printMessage: suspend (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        application.assets.list(assetDirName)?.forEach { name ->
            val assetPath = "$assetDirName/$name"
            println("Processing $assetPath...")
            val destination = File(destDir, name)
            println("Copying $assetPath to $destination...")
            printMessage("Copying $name...\n")
            application.assets.open(assetPath).use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            println("Copied $assetPath to $destination")
        }
    }

    /*fun createContextFromFile(filePath: String): WhisperContext {
        val tempPtr = WhisperLib.initContext(filePath)
        if (tempPtr == 0L) {
            throw java.lang.RuntimeException("Couldn't create context with path $filePath")
        }
        ptr = tempPtr
    }

    fun createContextFromInputStream(stream: InputStream): WhisperContext {
        val tempPtr = WhisperLib.initContextFromInputStream(stream)

        if (tempPtr == 0L) {
            throw java.lang.RuntimeException("Couldn't create context from input stream")
        }
        ptr = tempPtr
    }*/

    override fun createContextFromAsset(assetPath: String) {
        val tempPtr = WhisperLib.initContextFromAsset(application.assets, assetPath)

        if (tempPtr == 0L) {
            println("Couldn't create context from asset $assetPath")
            throw java.lang.RuntimeException("Couldn't create context from asset $assetPath")
        }

        ptr.update {
            tempPtr
        }
    }

    override fun getSystemInfo(): String {
        return WhisperLib.getSystemInfo()
    }
}

private class WhisperLib {
    companion object {
        init {
            Log.d(LOG_TAG, "Primary ABI: ${Build.SUPPORTED_ABIS[0]}")
            var loadVfpv4 = false
            var loadV8fp16 = false
            if (isArmEabiV7a()) {
                // armeabi-v7a needs runtime detection support
                val cpuInfo = cpuInfo()
                cpuInfo?.let {
                    Log.d(LOG_TAG, "CPU info: $cpuInfo")
                    if (cpuInfo.contains("vfpv4")) {
                        Log.d(LOG_TAG, "CPU supports vfpv4")
                        loadVfpv4 = true
                    }
                }
            } else if (isArmEabiV8a()) {
                // ARMv8.2a needs runtime detection support
                val cpuInfo = cpuInfo()
                cpuInfo?.let {
                    Log.d(LOG_TAG, "CPU info: $cpuInfo")
                    if (cpuInfo.contains("fphp")) {
                        Log.d(LOG_TAG, "CPU supports fp16 arithmetic")
                        loadV8fp16 = true
                    }
                }
            }

            if (loadVfpv4) {
                Log.d(LOG_TAG, "Loading libwhisper_vfpv4.so")
                System.loadLibrary("whisper_vfpv4")
            } else if (loadV8fp16) {
                Log.d(LOG_TAG, "Loading libwhisper_v8fp16_va.so")
                System.loadLibrary("whisper_v8fp16_va")
            } else {
                Log.d(LOG_TAG, "Loading libwhisper.so")
                System.loadLibrary("whisper")
            }
        }

        // JNI methods
        external fun initContextFromInputStream(inputStream: InputStream): Long
        external fun initContextFromAsset(assetManager: AssetManager, assetPath: String): Long
        external fun initContext(modelPath: String): Long
        external fun freeContext(contextPtr: Long)
        external fun fullTranscribe(contextPtr: Long, numThreads: Int, audioData: FloatArray)
        external fun getTextSegmentCount(contextPtr: Long): Int
        external fun getTextSegment(contextPtr: Long, index: Int): String
        external fun getTextSegmentT0(contextPtr: Long, index: Int): Long
        external fun getTextSegmentT1(contextPtr: Long, index: Int): Long
        external fun getSystemInfo(): String
        external fun benchMemcpy(nthread: Int): String
        external fun benchGgmlMulMat(nthread: Int): String
    }
}

//  500 -> 00:05.000
// 6000 -> 01:00.000
private fun toTimestamp(t: Long, comma: Boolean = false): String {
    var msec = t * 10
    val hr = msec / (1000 * 60 * 60)
    msec -= hr * (1000 * 60 * 60)
    val min = msec / (1000 * 60)
    msec -= min * (1000 * 60)
    val sec = msec / 1000
    msec -= sec * 1000

    val delimiter = if (comma) "," else "."
    return String.format("%02d:%02d:%02d%s%03d", hr, min, sec, delimiter, msec)
}

private fun isArmEabiV7a(): Boolean {
    return Build.SUPPORTED_ABIS[0].equals("armeabi-v7a")
}

private fun isArmEabiV8a(): Boolean {
    return Build.SUPPORTED_ABIS[0].equals("arm64-v8a")
}

private fun cpuInfo(): String? {
    return try {
        File("/proc/cpuinfo").inputStream().bufferedReader().use {
            it.readText()
        }
    } catch (e: Exception) {
        Log.w(LOG_TAG, "Couldn't read /proc/cpuinfo", e)
        null
    }
}