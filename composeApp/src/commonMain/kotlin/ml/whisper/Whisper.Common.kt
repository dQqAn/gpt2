package ml.whisper

expect class Whisper{
    interface IWhisperListener {
        fun onUpdateReceived(message: String?)
        fun onResultReceived(result: String?)
    }
}