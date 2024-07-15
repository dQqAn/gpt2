sealed class Screen(val route: String) {

    object Splash : Screen("splash_screen")

    object OnBoarding : Screen("onboarding_screen")

    object Message : Screen("message_screen") {
//        val chatID:String?= null
    }

    object Chat : Screen("chat_screen") {
//        val chatID:String?= null
    }

}