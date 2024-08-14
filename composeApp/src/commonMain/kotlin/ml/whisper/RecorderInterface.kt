package ml.whisper

import java.io.File

interface RecorderInterface {
    suspend fun startRecording(outputFile: File, onError: (Exception) -> Unit): Unit?
    suspend fun stopRecording()
}