import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryPickerBottomSheetScaffold(
    scaffoldState: BottomSheetScaffoldState,
    sheetShape: Shape = BottomSheetDefaults.ExpandedShape,
    sheetContainerColor: Color = BottomSheetDefaults.ContainerColor,
    sheetContentColor: Color = contentColorFor(sheetContainerColor),
    sheetTonalElevation: Dp = BottomSheetDefaults.Elevation,
    bottomSheetTitle: @Composable () -> Unit,
    onItemSelected: (country: Country) -> Unit,
    countryList: List<Country?>?,
    searchTextFieldText: String,
    iconDescription: String,
    titleText: String,
    onDismissRequest: () -> Unit,
    isBottomSheetVisible: Boolean,
) {
    val searchValue = rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    if (countryList?.first() != null) {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize().then(
                if (isBottomSheetVisible) Modifier.clickable {
                    onDismissRequest()
                } else Modifier
            ),
            sheetContent = {
                bottomSheetTitle()
                CountrySearchView(
                    searchValue, onSearch = {
                        searchValue.value = it
                    },
                    searchTextFieldText = searchTextFieldText,
                    iconDescription = iconDescription
                )
                Countries(searchValue.value, onItemSelected, countryList, onDismissRequest)
            },
            scaffoldState = scaffoldState,
            sheetSwipeEnabled = false,
            sheetDragHandle = {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = titleText
                )
            },
            sheetShape = sheetShape,
            sheetTonalElevation = sheetTonalElevation,
            sheetContainerColor = sheetContainerColor,
            sheetContentColor = sheetContentColor,
            content = {
//                scope.launch {
                LaunchedEffect(scope) {
                    scaffoldState.bottomSheetState.expand()
                }
            }
        )
    } else {
        onDismissRequest()
    }
}

@Composable
private fun Countries(
    searchValue: String,
    onItemSelected: (country: Country) -> Unit,
    countryList: List<Country?>?,
    onDismissRequest: () -> Unit,
) {
    val defaultCountries = remember(countryList) { countryList?.toMutableList() }

    val displayState = remember {
        if (countryList?.first() != null) {
            mutableStateOf(true)
        } else {
            mutableStateOf(false)
        }
    }

    LaunchedEffect(countryList) {
        if (countryList?.first() != null) {
            displayState.value = true
        }
    }

    if (displayState.value) {
        val countries = remember(searchValue) {
            if (searchValue.isEmpty()) {
                defaultCountries
            } else {
                defaultCountries.searchCountryList(searchValue)
            }
        }

        if ((countries?.isNotEmpty() == true) && (countries.first() != null)) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .background(Color.Black.copy(0.2f))
                    .nestedScroll(object : NestedScrollConnection {
                        override fun onPostScroll(
                            consumed: Offset,
                            available: Offset,
                            source: NestedScrollSource,
                        ) = available
                    })
            ) {
//                itemsIndexed(countries){index, item -> }
                items(
                    count = countries.size,
                    key = { it }
                ) { countryIndex ->
                    countries[countryIndex]?.let {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onItemSelected(it)
                                    onDismissRequest()
                                }
                                .padding(12.dp)
                        ) {
                            //Text(text = localeToEmoji(country.code))
                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(2f)
                            )
                            Text(
                                text = it.areaCode,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                        }
                        Divider(color = Color.LightGray, thickness = 0.5.dp)
                    }
                }
            }
        }
    } else {
        onDismissRequest()
    }
}
