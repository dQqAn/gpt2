import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MessengerItemCard(
    modifier: Modifier = Modifier,
    content: String,
    contentType: String,
    chatViewModel: ChatViewModel,
    maxWidth: Dp
) {
    when (contentType) {
        contentTypeImage -> {
            val imageBytes by chatViewModel.selectedByteArrayImages.collectAsState()

            Surface(
                modifier = modifier.padding(4.dp).width((maxWidth / 100 * 70)),
                color = BluePrimary,
                shape = RoundedCornerShape(topStart = 25.dp, bottomEnd = 25.dp, bottomStart = 25.dp)
            ) {
                imageBytes?.let {
                    chatViewModel.createBitmapFromFileByteArray(it)?.let { bitmap ->
                        Column(
//                            modifier = Modifier.fillMaxSize().width((maxWidth / 100 * 70)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier.clickable {
                                    chatViewModel.imageClassify(bitmap)
                                },
                                contentDescription = "",
                                bitmap = bitmap.asImageBitmap()
                            )
                            chatViewModel.imageClassifierResult.value?.let { classifierResult ->
                                Text(classifierResult)
                            }
                        }
                    }
                }
            }
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
