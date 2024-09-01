package presentation.screen

import BluePrimary
import LoginViewModel
import MessageViewModel
import Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import util.GetCurrentDate
import util.Localization
import viewmodel.MessageToChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxWithConstraintsScope.MessageScreen(
    navController: NavController,
    localization: Localization,
    viewModel: MessageViewModel = viewModel(),
    sharedVM: MessageToChatViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel()
//    gpt2Client: GPT2Client = viewModel()
) {
    val maxWidth = maxWidth
    val openGallery = mutableStateOf(false)
    val showRationalDialog = mutableStateOf(false)
    viewModel.takePermission(openGallery, showRationalDialog)

    val chats by viewModel.chats.collectAsState()

    //Collecting states from ViewModel
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchedList by viewModel.searchedList.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(Modifier.fillMaxWidth()) {
                SearchBar(
                    placeholder = {
                        Text(localization.search)
                    },
                    query = searchText,//text showed on SearchBar
                    onQueryChange = viewModel::onSearchTextChange, //update the value of searchText
                    onSearch = viewModel::onSearchTextChange, //the callback to be invoked when the input service triggers the ImeAction.Search action
                    active = isSearching, //whether the user is searching or not
                    onActiveChange = { viewModel.onToogleSearch() }, //the callback to be invoked when this search bar's active state is changed
                    modifier = Modifier
                        .widthIn(
                            max = (if (!isSearching) {
                                (maxWidth / 100 * 80)
                            } else {
                                maxWidth
                            })
                        )
                        .padding(
                            start = 16.dp,
                            top = 16.dp,
                            end = 4.dp,
                            bottom = 16.dp
                        )
                        .align(Alignment.CenterVertically)
                ) {
                    LazyColumn {
                        items(searchedList.size) { mail ->
                            searchedList[mail]?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(
                                        start = 8.dp,
                                        top = 4.dp,
                                        end = 8.dp,
                                        bottom = 4.dp
                                    ).clickable {
                                        viewModel.changeIsSearching()
                                        viewModel.changeSearchText(it)
                                        sharedVM.changeIsNewChat(false)
                                        sharedVM.changeOtherUserMail(it)
                                        sharedVM.changeChatID(null)
                                        val isNewChat = false
                                        navController.navigate(
                                            route = Screen.Chat.route + "?chatID=" + "&isNewChat=$isNewChat"
                                        ) {
                                            popUpTo(
                                                Screen.Chat.route + "?chatID=" + "&isNewChat=$isNewChat"
                                            ) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                if (!isSearching) {
                    IconButton(
                        modifier = Modifier
                            .widthIn(max = (maxWidth / 100 * 40))
                            .heightIn(max = 100.dp)
                            .padding(
                                start = 4.dp,
                                top = 16.dp,
                                end = 8.dp,
                                bottom = 16.dp
                            )
                            .align(Alignment.CenterVertically),
                        onClick = { showBottomSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
//                        tint = Color.Black.copy(0.3f),
                            contentDescription = ""
                        )
                    }
                }
            }
        },
        floatingActionButton = {

        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
//                verticalAlignment = Alignment.Bottom
            ) {
                /*Button(onClick = {
                    gpt2Client.launchAutocomplete()
                }) {
                    Text("Gpt2 Test")
                }*/

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    onClick = {
                        val currentDate = GetCurrentDate()
                        val isNewChat = true
                        navController.navigate(route = Screen.Chat.route + "?chatID=$currentDate gpt" + "&isNewChat=$isNewChat") {
                            popUpTo(Screen.Chat.route + "?chatID=$currentDate gpt" + "&isNewChat=$isNewChat") {
                                inclusive = true
                            }
                        }.apply {
                            sharedVM.changeChatID("$currentDate gpt")
                            sharedVM.changeIsNewChat(true)
                            sharedVM.changeOtherUserMail("GPT")
                            viewModel.otherUserID(null)
                        }
                    }) {
                    Text(modifier = Modifier, text = localization.newChat)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
//                horizontalAlignment = Alignment.End
            ) {
                items(chats.size) { index ->
                    val chatID = chats[index]
                    chatID?.let {
                        Box(
                            modifier = Modifier.clickable(onClick = {
                                val isNewChat = false
                                navController.navigate(route = Screen.Chat.route + "?chatID=" + chatID + "&isNewChat=$isNewChat") {
                                    popUpTo(Screen.Chat.route + "?chatID=" + chatID + "&isNewChat=$isNewChat") {
                                        inclusive = true
                                    }
                                }.apply {
                                    sharedVM.changeChatID(chatID)
                                    sharedVM.changeIsNewChat(false)
                                    val tempMail = chatID.split("_")
                                    if (tempMail.contains(viewModel.currentUserMail)) {
                                        val friendMail =
                                            if (tempMail.last() != viewModel.currentUserMail) tempMail.last() else tempMail.first()
                                        sharedVM.changeOtherUserMail(friendMail)
                                        viewModel.otherUserID(friendMail)
                                    } else {
                                        viewModel.otherUserID(null)
                                        sharedVM.changeOtherUserMail(null)
                                    }
                                }
                            }).fillMaxWidth()
                                .background(color = Color.LightGray)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(modifier = Modifier.align(Alignment.CenterStart), text = it)
                            IconButton(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                onClick = {
                                    viewModel.deleteChat(chatId = it)
                                }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    }
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxHeight(),
                    sheetState = sheetState,
                    onDismissRequest = { showBottomSheet = false }
                ) {
                    Button(onClick = {
                        loginViewModel.signOut()
                        navController.navigate(route = Screen.SignIn.route)
                    }) {
                        Text(localization.signOut)
                    }
                    Button(
                        onClick = {
                            openGallery.value = true
                        }
                    ) {
                        Text(localization.takePermission)
                    }
                    Button(
                        onClick = {
                            navController.navigate(route = Screen.Language.route) {
                                popUpTo(Screen.Language.route) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Text(localization.selectYourLanguage)
                    }

                    /*Button(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }) {
                        Text("Hide bottom sheet")
                    }*/
                }
            }
        }
    }
}