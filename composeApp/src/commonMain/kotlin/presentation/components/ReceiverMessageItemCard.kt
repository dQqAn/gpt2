import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ReceiverMessageItemCard(
    modifier: Modifier = Modifier,
    content: String,
    contentType: String,
    chatViewModel: ChatViewModel,
    maxWidth: Dp
) {
    when (contentType) {
        contentTypeImage -> {
            val selectedByteArrayImages: MutableState<ByteArray?> = mutableStateOf(null)
            chatViewModel.getFile(content, selectedByteArrayImages)

            val classifyText: MutableState<String?> = mutableStateOf(null)

            Box(modifier = modifier) {
                Row(
                    modifier = modifier.padding(4.dp)/*.width((maxWidth / 100 * 70))*/
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Bottom),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .size(18.dp),
                            painter = painterResource(id = MppR.drawable.ic_launcher),
                            contentDescription = ""
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp, bottomEnd = 25.dp),
                        color = GrayColor
                    ) {
                        selectedByteArrayImages.value?.let {
                            chatViewModel.createBitmapFromFileByteArray(it)?.let { bitmap ->
                                Column(
//                                modifier = Modifier.fillMaxSize().width((maxWidth / 100 * 70)),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        modifier = Modifier.clickable {
                                            chatViewModel.imageClassify(bitmap, classifyText)
                                        },
                                        contentDescription = "",
                                        bitmap = bitmap.asImageBitmap()
                                    )
                                    classifyText.value?.let { classifierResult ->
                                        Text(classifierResult)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        contentTypeMessage -> {
            Box(modifier = modifier) {
                Row(
                    modifier = modifier.padding(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Bottom),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .size(18.dp),
                            painter = painterResource(id = MppR.drawable.ic_launcher),
                            contentDescription = ""
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp, bottomEnd = 25.dp),
                        color = GrayColor
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 24.dp),
                            text = content,
                            style = MaterialTheme.typography.labelLarge.copy(color = Color(0xFF505050))
                        )
                    }
                }
            }
        }
    }
}

/*@Preview
@Composable
fun ReceiverMessageItemPreview() {
    ReceiverMessageItemCard(
        modifier = Modifier.fillMaxWidth()
    )
}*/

