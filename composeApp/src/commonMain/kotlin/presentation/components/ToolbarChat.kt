import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ToolbarChat(
    modifier: Modifier = Modifier,
    navController: NavController,
    friendName: String? = "GPT"
//    viewModel: ChatViewModel = viewModel()
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Image(
                modifier = Modifier.align(Alignment.CenterVertically).clickable {
                    navController.popBackStack()
//                    viewModel.changeDate("")
                },
                imageVector = Icons.Default.ArrowBack,
                contentDescription = ""
            )

            Image(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 12.dp),
                painter = painterResource(id = MppR.drawable.ic_launcher),
                contentDescription = ""
            )

            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = friendName ?: "GPT",
                    fontSize = 20.sp,
                    color = BluePrimary,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                if (friendName == "GPT") {
                    Row {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(6.dp)
                                .background(color = GreenColor, shape = CircleShape)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Online",
                            color = GreenColor,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp), color = GrayColor
        )
    }
}

@Preview
@Composable
fun ToolbarChatPreview() {
    ToolbarChat(navController = rememberNavController())
}

