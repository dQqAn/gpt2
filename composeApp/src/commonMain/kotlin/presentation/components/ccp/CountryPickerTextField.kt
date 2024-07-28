import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun CountryPickerTextField(
    label: String = "",
    modifier: Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    selectedCountry: Country? = null,
    defaultCountry: Country? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledBorderColor = MaterialTheme.colorScheme.outline,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
    isPickerVisible: Boolean = false,
    onShowCountryPicker: () -> Unit,
    countryList: List<Country?>?,
    iconDescription: String,
) {
    val defaultSelectedCountry = remember(countryList) {
        mutableStateOf(defaultCountry ?: countryList?.first())
    }

    val countryValue = "${defaultSelectedCountry.value?.areaCode} ${defaultSelectedCountry.value?.name}"

    OutlinedTextField(
        modifier = modifier.clickable { onShowCountryPicker() },
        enabled = false,
        readOnly = true,
        label = { Text(label) },
        value = if (selectedCountry == null) countryValue else "${selectedCountry.areaCode} ${selectedCountry.name}",
        onValueChange = {},
        colors = colors,
        shape = shape,
        trailingIcon = {
            Icon(
                Icons.Filled.ArrowDropDown,
                iconDescription,
                Modifier.graphicsLayer {
                    rotationZ = if (isPickerVisible) 180f else 0f
                }
            )
        }
    )
}
