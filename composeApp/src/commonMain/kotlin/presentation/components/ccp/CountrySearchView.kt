import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun CountrySearchView(
    searchValue: MutableState<String>,
    onSearch: (searchValue: String) -> Unit,
    searchTextFieldText: String,
    iconDescription: String,
) {

    val focusManager = LocalFocusManager.current

    Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)) {
        Box(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(
                        Color.LightGray.copy(0.6f), shape = RoundedCornerShape(10.dp)
                    ),
                value = searchValue.value,
                onValueChange = {
                    onSearch(it)
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp
                ),
                placeholder = {
                    Text(
                        text = searchTextFieldText,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        fontSize = 16.sp,
                    )
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Black.copy(0.3f)
                    )
                },
                trailingIcon = {
                    if (searchValue.value.isNotEmpty()) {
                        IconButton(onClick = {
                            onSearch("")
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                tint = Color.Black.copy(0.3f),
                                contentDescription = iconDescription
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
        }
    }
}