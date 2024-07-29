sealed class Screen(val route: String) {

    object Splash : Screen("splash_screen")

    object OnBoarding : Screen("onboarding_screen")

    object Message : Screen("message_screen") {
//        val chatID:String?= null
    }

    object Chat : Screen("chat_screen") {
//        val chatID:String?= null
    }

    object SignIn : Screen("sign_in_screen")

    object SignUp : Screen("sign_up_screen")

    object MailVerification : Screen("mail_verification_screen")

    object PhoneVerification : Screen("phone_verification_screen")

    object ForgotPassword : Screen("forgot_password_screen")
}