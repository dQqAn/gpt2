package ml.whisper

import java.io.File

interface WhisperContextInterface {
    val getFilesDir: File

    //    val models: Array<out String>?
    val models: Array<String?>?
    fun getSystemInfo(): String
    suspend fun copyData(
        assetDirName: String,
        destDir: File,
        printMessage: suspend (String) -> Unit
    ): Unit?

    fun createContextFromAsset(assetPath: String)

    suspend fun benchMemory(nthreads: Int): String
    suspend fun benchGgmlMulMat(nthreads: Int): String
    suspend fun stopPlayback()
    suspend fun startPlayback(file: File): Unit?
    suspend fun transcribeData(data: FloatArray, printTimestamp: Boolean = true): String
    suspend fun release()
}