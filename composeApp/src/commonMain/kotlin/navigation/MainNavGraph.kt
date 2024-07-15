import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import presentation.screen.MessageScreen
import viewmodel.MessageToChatViewModel

@Composable
fun MainNavGraph(navController: NavHostController) {
    val viewModel: MessageToChatViewModel = viewModel()

    NavHost(
        navController = navController,
        route = "main_route",
        startDestination = Screen.Splash.route
    ) {
        //intro screen
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        //entrance screen
        composable(route = Screen.OnBoarding.route) {
            OnBoardingScreen(navController = navController)
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
                sharedVM = viewModel
            )
        }

        //chat screen
//        composable(route = Screen.Chat.route + "/{chatID}") { backStackEntry ->
        composable(route = Screen.Chat.route) {
//            val chatID = backStackEntry.arguments?.getString("chatID")

//            val args = it.toRoute<Screen.Message>()
            ChatScreen(
                navController = navController,
                sharedVM = viewModel
//                argument = chatID
//                chatID=args.chatID
            )
        }
    }

}