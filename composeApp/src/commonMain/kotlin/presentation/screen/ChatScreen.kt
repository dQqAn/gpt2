import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import util.Localization
import viewmodel.MessageToChatViewModel

//import ml.gpt2.*

@Composable
fun BoxWithConstraintsScope.ChatScreen(
    navController: NavController,
    localization: Localization,
    chatViewModel: ChatViewModel = viewModel(),
    sharedVM: MessageToChatViewModel = viewModel(),
//    gpt2Client: GPT2Client = viewModel()
) {
    val maxWidth = maxWidth

    val openGallery = mutableStateOf(false)
    val showRationalDialog = mutableStateOf(false)
    chatViewModel.launchGallery(openGallery, showRationalDialog)

    val isNewChat = sharedVM.isNewChat.value
    val otherUserMail = sharedVM.otherUserMail.value
    val senderID = chatViewModel.senderID.value
    val receiverID = chatViewModel.receiverID.value
    val friendID by chatViewModel.friendID.collectAsState()
    val currentUserMail = chatViewModel.currentUserMail
    val currentUserID = chatViewModel.currentUserID
    val chatID = sharedVM.chatID.value ?: (currentUserMail + "_" + otherUserMail)

    val localMessageList by chatViewModel.localMessageList.collectAsState()
    val remoteMessageList by chatViewModel.remoteMessageList.collectAsState()

    LaunchedEffect(remoteMessageList) {
        chatViewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                for (item in remoteMessageList) {
                    if (localMessageList.find { it?.messageID == item?.messageID } == null) {
                        item?.let {
                            val tempChatID = if (localMessageList.isNotEmpty()) {
                                localMessageList.first()?.chatID!!
                            } else {
                                chatID
                            }
                            chatViewModel.localAddAnswer(
                                AnswerEntity(
                                    id = it.id,
                                    chatID = tempChatID,
                                    messageID = it.messageID,
                                    role = it.role,
                                    contentType = it.contentType,
                                    content = it.content,
                                    senderID = it.senderID,
                                    receiverID = it.receiverID,
                                    date = it.date
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    friendID?.let {
        chatViewModel.getAnswer(chatID, currentUserID, friendID)
    }

    val loading by chatViewModel.loading.collectAsState()

//    val (input, setInput) = remember { mutableStateOf("") }
    val input = chatViewModel.messageText.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ToolbarChat(friendName = otherUserMail, navController = navController)
        },
        floatingActionButton = {

        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            WriteMessageCard(
                localization = localization,
                galleryImages = chatViewModel.selectedImages,
                chatViewModel = chatViewModel,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                value = input.value ?: "",
                onValueChange = { value ->
                    chatViewModel.changeMessageText(value)
                },
                onClickCamera = {
                    navController.navigate(route = Screen.Camera.route) {
                        popUpTo(Screen.Camera.route) {
                            inclusive = true
                        }
                    }
                },
                onClickGallery = {
                    openGallery.value = true
                },
                onClickSend = {
                    var _chatID: String? = null
                    if (localMessageList.isNotEmpty()) {
                        _chatID = localMessageList.first()?.chatID!!
                    } else {
                        _chatID = chatID
                    }

                    if (!input.value.isNullOrEmpty() && input.value!!.isNotBlank()) {
                        val aiChatControl = _chatID.split(" ").last()
                        if (aiChatControl != "gpt") {
                            chatViewModel.addAnswer(
                                content = input.value!!,
                                contentType = contentTypeMessage,
                                chatID = _chatID,
                                senderID = currentUserID,
                                receiverID = friendID!!
                            )
                        } else {
                            chatViewModel.newChatAiQuestion(
                                question = input.value!!,
                                contentType = contentTypeMessage,
                                chatID = _chatID,
                                senderID = currentUserID,
                                receiverID = "gpt"
                            )
                        }
                        chatViewModel.changeMessageText("")

//                        gpt2Client.launchAutocomplete()
                    } else if (chatViewModel.selectedImages.value.isNotEmpty()) {
                        chatViewModel.uploadFiles(
                            contentType = contentTypeImage,
                            chatID = _chatID,
                            senderID = currentUserID,
                            receiverID = friendID!!
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                /*.padding(top = 8.dp)*/,
//                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                itemsIndexed(localMessageList) { index, data ->
                    data?.let {
                        if (it.senderID == currentUserID && it.fromUser) {
                            MessengerItemCard(
                                modifier = Modifier.align(Alignment.End),
                                contentType = it.contentType,
                                content = it.content,
                                chatViewModel = chatViewModel,
                                maxWidth = maxWidth
                            )
                        } else {
                            ReceiverMessageItemCard(
                                modifier = Modifier.align(Alignment.Start),
                                contentType = it.contentType,
                                content = it.content,
                                chatViewModel = chatViewModel,
                                maxWidth = maxWidth
                            )
                        }
                    }
                }

                /*if (isNewChat) {
                    items(messages.size) { index ->
                        val message = messages[index]
                        if (message!!.fromUser) {
                            MessengerItemCard(
                                modifier = Modifier.align(Alignment.End),
                                message = message.content
                            )
                        } else {
                            ReceiverMessageItemCard(message = message.content)
                        }
                    }
                } else {
                    items(messageList.size) { index ->
                        messageList[index]?.let {
                            if (it.senderID == currentUserID) {
                                MessengerItemCard(
                                    modifier = Modifier.align(Alignment.End),
                                    message = it.content
                                )
                            } else {
                                ReceiverMessageItemCard(message = it.content)
                            }
                        }
                    }
                }*/
            }
        }
    }
}

/*
@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        navController = rememberNavController(),
    )
}*/
