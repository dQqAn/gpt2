import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import presentation.screen.MessageScreen
import viewmodel.LocalizationViewModel
import viewmodel.MessageToChatViewModel

@Composable
fun MainNavGraph() {
    BoxWithConstraints {
        val messageToChatViewModel: MessageToChatViewModel = viewModel()
        val loginViewModel: LoginViewModel = viewModel()
        val localizationViewModel: LocalizationViewModel = viewModel()

        val navController: NavHostController = rememberNavController()
        loginViewModel.setNavController(navController)

        val localization = getCurrentLocalization(localizationViewModel)

        localization?.let {
            NavHost(
                navController = navController,
                route = "main_route",
//            startDestination = Screen.SignIn.route
                startDestination = loginViewModel.getPage().route
            ) {
                //intro screen
                composable(route = Screen.Splash.route) {
                    SplashScreen(
                        navController = navController,
                        localization = localization
                    )
                }

                //entrance screen
                composable(route = Screen.OnBoarding.route) {
                    OnBoardingScreen(
                        navController = navController,
                        localization = localization
                    )
                }

                //message screen
                composable(
                    route = Screen.Message.route,
                    /*arguments = listOf(navArgument("chatID") {
                        type = NavType.StringType
                    })*/
                ) {
                    MessageScreen(
                        navController = navController,
                        sharedVM = messageToChatViewModel,
                        loginViewModel = loginViewModel,
                        localization = localization
                    )
                }

                //chat screen
//        composable(route = Screen.Chat.route + "/{chatID}") { backStackEntry ->
                composable(route = Screen.Chat.route) {
//            val chatID = backStackEntry.arguments?.getString("chatID")

//            val args = it.toRoute<Screen.Message>()
                    ChatScreen(
                        navController = navController,
                        sharedVM = messageToChatViewModel,
                        localization = localization
//                argument = chatID
//                chatID=args.chatID
                    )
                }

                //sign in screen
                composable(route = Screen.SignIn.route) {
                    SignInContent(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        localization = localization
                    )
                }

                // sign up screen
                composable(route = Screen.SignUp.route) {
                    SignUpContent(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        localization = localization
                    )
                }

                // mail verification screen
                composable(route = Screen.MailVerification.route) {
                    MailVerificationContent(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        localization = localization
                    )
                }

                // phone verification screen
                composable(route = Screen.PhoneVerification.route) {
                    PhoneVerificationContent(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        localization = localization
                    )
                }

                //forgot password screen
                composable(route = Screen.ForgotPassword.route) {
                    ForgotPasswordContent(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        localization = localization
                    )
                }

                //language screen
                composable(route = Screen.Language.route) {
                    LanguageContent(
                        navController,
                        localizationViewModel,
                    )
                }

                composable(route = Screen.Camera.route) {
                    CameraPage(navController = navController, localization = localization)
                }
            }
        }
    }
}