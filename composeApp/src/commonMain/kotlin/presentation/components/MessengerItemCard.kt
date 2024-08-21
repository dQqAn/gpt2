import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MessengerItemCard(
    modifier: Modifier = Modifier,
    content: String,
    contentType: String
) {
    when (contentType) {
        contentTypeImage -> {

        }

        contentTypeMessage -> {
            Surface(
                modifier = modifier.padding(4.dp),
                color = BluePrimary,
                shape = RoundedCornerShape(topStart = 25.dp, bottomEnd = 25.dp, bottomStart = 25.dp)
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    text = content,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                )
            }
        }
    }
}

/*
@Preview
@Composable
fun MessengerItemPreview() {
    MessengerItemCard()
}*/
