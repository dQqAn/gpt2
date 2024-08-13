package ml.whisper

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.koin.core.component.KoinComponent
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

actual class Whisper(
    private val mContext: Context,
    private val listener: IWhisperListener
) {
    private val mInProgress = AtomicBoolean(false)
    private val mAudioBufferQueueLock = Any() // Synchronization object
    private val mWhisperEngineLock = Any() // Synchronization object
    private val audioBufferQueue: Queue<FloatArray> = LinkedList()
    private var mMicTranscribeThread: Thread? = null

    private val mWhisperEngine: IWhisperEngine = WhisperEngine(mContext)

    private var mAction: String? = null
    private var mWavFilePath: String? = null
    private var mExecutorThread: Thread? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadModel(modelPath: String?, vocabPath: String?, isMultilingual: Boolean) {
        try {
            mWhisperEngine.initialize(modelPath, vocabPath, isMultilingual)

            // Start thread for mic data transcription in realtime
            startMicTranscriptionThread()
        } catch (e: IOException) {
            Log.e(TAG, "Error...", e)
        }
    }

    fun setAction(action: String?) {
        mAction = action
    }

    fun setFilePath(wavFile: String?) {
        mWavFilePath = wavFile
    }

    fun start() {
        if (mInProgress.get()) {
            Log.d(TAG, "Execution is already in progress...")
            return
        }

        mExecutorThread = Thread {
            mInProgress.set(true)
            threadFunction()
            mInProgress.set(false)
        }
        mExecutorThread!!.start()
    }

    fun stop() {
        mInProgress.set(false)
        try {
            if (mExecutorThread != null) {
                mExecutorThread!!.interrupt();
                mExecutorThread!!.join()
                mExecutorThread = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    val isInProgress: Boolean
        get() = mInProgress.get()

    private fun threadFunction() {
        try {
            // Get Transcription
            if (mWhisperEngine.isInitialized) {
                Log.d(TAG, "WaveFile: $mWavFilePath")

                val waveFile = File(mWavFilePath)
                if (waveFile.exists()) {
                    val startTime = System.currentTimeMillis()
                    listener.onUpdateReceived(MSG_PROCESSING)

                    //                    String result = "";
//                    if (mAction.equals(ACTION_TRANSCRIBE))
//                        result = mWhisperEngine.getTranscription(mWavFilePath);
//                    else if (mAction == ACTION_TRANSLATE)
//                        result = mWhisperEngine.getTranslation(mWavFilePath);

                    // Get result from wav file
                    synchronized(mWhisperEngineLock) {
                        val result = mWhisperEngine.transcribeFile(mWavFilePath)
                        listener.onResultReceived(result)
                        Log.d(
                            TAG,
                            "Result len: " + result.length + ", Result: " + result
                        )
                    }

                    listener.onUpdateReceived(MSG_PROCESSING_DONE)

                    // Calculate time required for transcription
                    val endTime = System.currentTimeMillis()
                    val timeTaken = endTime - startTime
                    Log.d(TAG, "Time Taken for transcription: " + timeTaken + "ms")
                } else {
                    listener.onUpdateReceived(MSG_FILE_NOT_FOUND)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error...", e)
            listener.onUpdateReceived(e.message)
        }
    }

    // Write buffer in Queue
    fun writeBuffer(samples: FloatArray) {
        synchronized(mAudioBufferQueueLock) {
            audioBufferQueue.add(samples)
            (mAudioBufferQueueLock as Object).notify() // Notify waiting threads
        }
    }

    // Read buffer from Queue
    private fun readBuffer(): FloatArray {
        synchronized(mAudioBufferQueueLock) {
            while (audioBufferQueue.isEmpty()) {
                try {
                    // Wait for the queue to have data
                    (mAudioBufferQueueLock as Object).wait()
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
            return audioBufferQueue.poll()
        }
    }

    // Mic data transcription thread in realtime
    private fun startMicTranscriptionThread() {
        if (mMicTranscribeThread == null) {
            // Create a transcribe thread
            mMicTranscribeThread = Thread {
                while (true) {
                    val samples = readBuffer()
                    if (samples != null) {
                        synchronized(mWhisperEngineLock) {
                            // TODO: null func
//                            val result: String = mWhisperEngine.transcribeBuffer(samples)
                            val result: String? = null
                            listener.onResultReceived(result)
                        }
                    }
                }
            }

            // Start the transcribe thread
            mMicTranscribeThread!!.start()
        }
    }

    companion object {
        const val TAG: String = "Whisper"
        const val ACTION_TRANSLATE: String = "TRANSLATE"
        const val ACTION_TRANSCRIBE: String = "TRANSCRIBE"
        const val MSG_PROCESSING: String = "Processing..."
        const val MSG_PROCESSING_DONE: String = "Processing done...!"
        const val MSG_FILE_NOT_FOUND: String = "Input file doesn't exist..!"
    }

    actual interface IWhisperListener {
        actual fun onUpdateReceived(message: String?)
        actual fun onResultReceived(result: String?)
    }
}