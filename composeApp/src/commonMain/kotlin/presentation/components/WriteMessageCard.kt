import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteMessageCard(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClickSend: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 6.dp,
        color = Color.White,
        shape = RoundedCornerShape(30.dp),
    ) {
        TextField(
            modifier = Modifier.background(color = Color.White),
            value = value,
            onValueChange = { value ->
                onValueChange(value)
            },
            placeholder = {
                Text(
                    text = "Write your message",
                    fontWeight = FontWeight.Bold
                )
            },
            trailingIcon = {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onClickSend()
                        },
                    painter = painterResource(id = MppR.drawable.ic_launcher),
                    contentDescription = ""
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedPlaceholderColor = GrayColor,
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

