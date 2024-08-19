import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import viewmodel.MessageToChatViewModel

//import ml.gpt2.*

@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = viewModel(),
    sharedVM: MessageToChatViewModel = viewModel(),
//    gpt2Client: GPT2Client = viewModel()
) {
    val isNewChat = sharedVM.isNewChat.value
    val otherUserMail = sharedVM.otherUserMail.value
    val senderID = chatViewModel.senderID.value
    val receiverID = chatViewModel.receiverID.value
    val friendID by chatViewModel.friendID.collectAsState()
    val currentUserMail = chatViewModel.currentUserMail
    val currentUserID = chatViewModel.currentUserID
    val chatID = sharedVM.chatID.value ?: (currentUserMail + "_" + otherUserMail)

    chatViewModel.loadMessages(chatID, senderID, receiverID, isNewChat)

    val messageList by chatViewModel.remoteMessageList.collectAsState()
    friendID?.let {
        chatViewModel.getAnswer(chatID, currentUserID, friendID)
    }

    val loading by chatViewModel.loading.collectAsState()

//    val (input, setInput) = remember { mutableStateOf("") }
    val input = chatViewModel.messageText

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
                chatViewModel = chatViewModel,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                value = input.value,
                onValueChange = { value ->
                    chatViewModel.changeMessageText(value)
                },
                onClickSend = {
                    if (input.value.isNotEmpty()) {
                        var _chatID: String? = null

                        if (messageList.isNotEmpty()) {
                            _chatID = messageList.first()?.chatID!!
                        } else {
                            _chatID = chatID
                        }

//                        println("1: "+currentUserID)
//                        println("2: "+friendID)
//                        viewModel.changeSenderID(currentUserID)
//                        viewModel.changeReceiverID(friendID!!)
//                        println("3: "+senderID)
//                        println("4: "+receiverID)


                        val aiChatControl = _chatID.split(" ").last()
                        if (aiChatControl != "gpt") {
                            chatViewModel.addAnswer(
                                message = input.value,
                                chatID = _chatID,
                                senderID = currentUserID,
                                receiverID = friendID!!
                            )
                        } else {
                            chatViewModel.newChatAiQuestion(
                                question = input.value,
                                chatID = _chatID,
                                senderID = currentUserID,
                                receiverID = "gpt"
                            )
                        }
                        chatViewModel.changeMessageText("")

//                        gpt2Client.launchAutocomplete()
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
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                items(messageList.size) { index ->
                    messageList[index]?.let {
                        if (it.senderID == currentUserID && it.fromUser) {
                            MessengerItemCard(
                                modifier = Modifier.align(Alignment.End),
                                message = it.content
                            )
                        } else {
                            ReceiverMessageItemCard(message = it.content)
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

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        navController = rememberNavController(),
    )
}