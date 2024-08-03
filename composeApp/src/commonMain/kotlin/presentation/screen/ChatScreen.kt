import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
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
    viewModel: ChatViewModel = viewModel(),
    sharedVM: MessageToChatViewModel = viewModel(),
//    gpt2Client: GPT2Client = viewModel()
) {
    val chatID = sharedVM.chatID.value!!
    val isNewChat = sharedVM.isNewChat.value
    val senderID = viewModel.senderID.value
    val receiverID = viewModel.receiverID.value

    viewModel.loadMessages(chatID, senderID, receiverID, isNewChat)

    val messages by viewModel.messages.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val (input, setInput) = remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ToolbarChat(navController = navController)
        },
        floatingActionButton = {

        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            WriteMessageCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                value = input,
                onValueChange = { value ->
                    setInput(value)
                },
                onClickSend = {
                    if (input.isNotEmpty()) {
                        var _chatID: String? = null

                        if (messages.isNotEmpty()) {
                            _chatID = messages.first().chatID!!
                        } else {
                            _chatID = chatID
                        }

                        val aiChatControl = _chatID.split(" ").last()
                        if (aiChatControl != "gpt") {
                            viewModel.addAnswer(
                                message = input,
                                chatID = _chatID,
                                receiverID = receiverID,
                                senderID = senderID
                            )
                        } else {
                            viewModel.newChatAiQuestion(
                                question = input,
                                chatID = _chatID,
                                senderID = senderID,
                                receiverID = receiverID
                            )
                        }
                        setInput("")

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
                items(messages.size) { index ->
                    val message = messages[index]
                    if (message.fromUser) {
                        MessengerItemCard(
                            modifier = Modifier.align(Alignment.End),
                            message = message.content
                        )
                    } else {
                        ReceiverMessageItemCard(message = message.content)
                    }
                }
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