import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SplashScreen(
    navController: NavController,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BluePrimary
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .height(262.dp)
                .fillMaxWidth(),
            painter = painterResource(id = MppR.drawable.ic_launcher),
            contentDescription = ""
        )
    }

    LaunchedEffect(Unit) {
//        delay(1200L)
        navController.popBackStack()
        navController.navigate(route = Screen.OnBoarding.route)
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}