import presentation.components.UserInterface

expect class UserRepository : UserInterface {
    interface ScreenListener {
        fun onError(error: String)
        fun onResults(screen: Screen)
    }
}