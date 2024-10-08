import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import util.Localization
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun WriteMessageCard(
    localization: Localization,
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClickSend: () -> Unit,
    onClickGallery: () -> Unit,
    onClickCamera: () -> Unit,
    chatViewModel: ChatViewModel,
    galleryImages: MutableState<List<File?>>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 6.dp,
        color = Color.White,
        shape = RoundedCornerShape(30.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                if (galleryImages.value.isNotEmpty()) {
                    Column {
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(galleryImages.value.size) { i ->
                                Column {
                                    Image(
//                                        modifier = Modifier.heightIn(max = 50.dp),
                                        bitmap = chatViewModel.createBitmapFromFilePath(galleryImages.value[i]!!.path)!!
                                            .asImageBitmap(), contentDescription = "", contentScale = ContentScale.Crop
                                    )
                                    Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                                        //todo(delete the list item )
                                    }) {
                                        Text(localization.delete)
                                    }
                                }
                            }
                        }
                        Row {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Button(modifier = Modifier.align(Alignment.CenterStart), onClick = {

                                }) {
                                    Text(localization.cancel)
                                }
                            }
                        }
                    }
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    TextField(
                        modifier = Modifier.background(color = Color.White),
                        value = value,
                        onValueChange = { value ->
                            onValueChange(value)
                        },
                        placeholder = {
                            Text(
                                text = localization.writeMessage, fontWeight = FontWeight.Bold
                            )
                        },
                        trailingIcon = {
                            IconButton(modifier = Modifier.size(24.dp), onClick = {
                                onClickSend()
                            }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            unfocusedPlaceholderColor = GrayColor,
                            containerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    IconButton(modifier = Modifier, onClick = {
                        onClickGallery()
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                    IconButton(modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_UP -> {
                                chatViewModel.stopSpeechToText()
                            }

                            MotionEvent.ACTION_DOWN -> {
                                chatViewModel.startSpeechToText()
                            }
                        }
                        true
                    }, onClick = {

                    }) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null)
                    }
                    IconButton(modifier = Modifier, onClick = {
                        onClickCamera()
                    }) {
                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null)
                    }
                }
            }
        }
    }
}
