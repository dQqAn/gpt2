import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OnBoardingScreen(
    navController: NavController
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 28.dp, end = 28.dp, top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You AI Assistant",
                    color = BluePrimary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Using this software,you can ask you\n" +
                            "questions and receive articles using\n" +
                            "artificial intelligence assistant",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 15.sp,
                        color = TextColorGray
                    )
                )

                Image(
                    modifier = Modifier
                        .height(400.dp)
                        .padding(top = 84.dp)
                        .fillMaxWidth(),
                    painter = painterResource(id = MppR.drawable.ic_launcher),
                    contentDescription = ""
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 34.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                onClick = {
                    navController.popBackStack()
                    navController.navigate(route = Screen.Message.route)
                }
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = "Continue",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                )

                Image(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.ArrowForward,
                    colorFilter = ColorFilter.tint(color = Color.White),
                    contentDescription = ""
                )

            }
        }
    }
}

@Preview
@Composable
fun OnBoardingScreePreview() {
    OnBoardingScreen(navController = rememberNavController())
}