import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun WriteMessageCard(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClickSend: () -> Unit,
    onClickGallery: () -> Unit,
    chatViewModel: ChatViewModel
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 6.dp,
        color = Color.White,
        shape = RoundedCornerShape(30.dp),
    ) {
        Box(modifier = modifier.fillMaxWidth()) {
            Row {
                TextField(
                    modifier = Modifier.background(color = Color.White).align(Alignment.CenterVertically),
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
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Row {
                        IconButton(modifier = Modifier,
                            onClick = {
                                onClickGallery()
                            }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                        IconButton(
                            modifier = Modifier.pointerInteropFilter {
                                when (it.action) {
                                    MotionEvent.ACTION_UP -> {
                                        chatViewModel.stopSpeechToText()
                                    }

                                    MotionEvent.ACTION_DOWN -> {
                                        chatViewModel.startSpeechToText()
                                    }
                                }
                                true
                            },
                            onClick = {

                            }) {
                            Icon(imageVector = Icons.Default.Build, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

