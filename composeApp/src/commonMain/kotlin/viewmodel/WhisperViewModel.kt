package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ml.whisper.RecorderInterface
import ml.whisper.WhisperContextInterface
import ml.whisper.decodeWaveFile
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.io.File

class WhisperViewModel() : ViewModel(), KoinComponent {
    var canTranscribe by mutableStateOf(false)
        private set
    var dataLog by mutableStateOf("")
        private set
    var isRecording by mutableStateOf(false)
        private set

    private val _ptr: MutableStateFlow<Int> = MutableStateFlow(0)
    val ptr = _ptr.asStateFlow()
    private val whisperContext: WhisperContextInterface by inject<WhisperContextInterface>() {
        parametersOf(_ptr)
    }

    private val modelsPath = File(whisperContext.getFilesDir, "models")
    private val samplesPath = File(whisperContext.getFilesDir, "samples")
    private val recorder: RecorderInterface by inject()

    private var recordedFile: File? = null

    init {
        viewModelScope.launch {
            printSystemInfo()
            loadData()
        }
    }

    private suspend fun printSystemInfo() {
        printMessage(String.format("System Info: %s\n", whisperContext.getSystemInfo()))
    }

    private suspend fun loadData() {
        printMessage("Loading data...\n")
        try {
            copyAssets()
            loadBaseModel()
            canTranscribe = true
        } catch (e: Exception) {
            println("loadData: $e")
            printMessage("${e.localizedMessage}\n")
        }
    }

    private suspend fun printMessage(msg: String) = withContext(Dispatchers.IO) {
        dataLog += msg
    }

    private suspend fun copyAssets() = withContext(Dispatchers.IO) {
        modelsPath.mkdirs()
        samplesPath.mkdirs()
        //application.copyData("models", modelsPath, ::printMessage)
        whisperContext.copyData("samples", samplesPath, ::printMessage)
        printMessage("All data copied to working directory.\n")
    }

    private suspend fun loadBaseModel() = withContext(Dispatchers.IO) {
        printMessage("Loading model...\n")
        val models = whisperContext.models
        if (models != null) {
            whisperContext.createContextFromAsset("models/" + models[0])
            printMessage("Loaded model ${models[0]}.\n")
        }

//        val firstModel = modelsPath.listFiles()!!.first()
//        whisperContext.createContextFromFile(firstModel.absolutePath)
    }

    fun benchmark() = viewModelScope.launch {
        runBenchmark(6)
    }

    fun transcribeSample() = viewModelScope.launch {
        transcribeAudio(getFirstSample())
    }

    private suspend fun runBenchmark(nthreads: Int) {
        if (!canTranscribe) {
            return
        }

        canTranscribe = false

        printMessage("Running benchmark. This will take minutes...\n")
        whisperContext.benchMemory(nthreads).let { printMessage(it) }
        printMessage("\n")
        whisperContext.benchGgmlMulMat(nthreads).let { printMessage(it) }

        canTranscribe = true
    }

    private suspend fun getFirstSample(): File = withContext(Dispatchers.IO) {
        samplesPath.listFiles()!!.first()
    }

    private suspend fun readAudioSamples(file: File): FloatArray = withContext(Dispatchers.IO) {
        stopPlayback()
        startPlayback(file)
        return@withContext decodeWaveFile(file)
    }

    private suspend fun stopPlayback() = whisperContext.stopPlayback()

    private suspend fun startPlayback(file: File) = whisperContext.startPlayback(file)

    private suspend fun transcribeAudio(file: File) {
        if (!canTranscribe) {
            return
        }

        canTranscribe = false

        try {
            printMessage("Reading wave samples... ")
            val data = readAudioSamples(file)
            printMessage("${data.size / (16000 / 1000)} ms\n")
            printMessage("Transcribing data...\n")
            val start = System.currentTimeMillis()
            val text = whisperContext.transcribeData(data)
            val elapsed = System.currentTimeMillis() - start
            printMessage("Done ($elapsed ms): \n$text\n")
        } catch (e: Exception) {
            println("transcribeAudio: $e")
            printMessage("${e.localizedMessage}\n")
        }

        canTranscribe = true
    }

    fun toggleRecord() = viewModelScope.launch {
        try {
            if (isRecording) {
                recorder.stopRecording()
                isRecording = false
                recordedFile?.let { transcribeAudio(it) }
            } else {
                stopPlayback()
                val file = getTempFileForRecording()
                recorder.startRecording(file) { e ->
                    viewModelScope.launch {
                        withContext(Dispatchers.Main) {
                            printMessage("${e.localizedMessage}\n")
                            isRecording = false
                        }
                    }
                }
                isRecording = true
                recordedFile = file
            }
        } catch (e: Exception) {
            println("toggleRecord: $e")
            printMessage("${e.localizedMessage}\n")
            isRecording = false
        }
    }

    private suspend fun getTempFileForRecording() = withContext(Dispatchers.IO) {
        File.createTempFile("recording", "wav")
    }

    override fun onCleared() {
        runBlocking {
            whisperContext.release()
            stopPlayback()
        }
    }

    /*companion object {
        fun factory() = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                MainScreenViewModel(application)
            }
        }
    }*/
}
