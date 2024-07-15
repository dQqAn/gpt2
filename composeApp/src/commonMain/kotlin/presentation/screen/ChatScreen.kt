import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel(),
    sharedVM: MessageToChatViewModel = viewModel()
) {
    viewModel.changeChatID(sharedVM.chatID.value)
    viewModel.loadMessages()

    val messages by viewModel.messages.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val (input, setInput) = remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ToolbarChat(navController = navController, viewModel = viewModel)
        },
        floatingActionButton = {
            WriteMessageCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                value = input,
                onValueChange = { value ->
                    setInput(value)
                },
                onClickSend = {
                    if (input.isNotEmpty()) {
                        var currentDate: String? = null

                        if (messages.isNotEmpty()) {
                            currentDate = messages.first().chatID!!
                            viewModel.askQuestion(
                                question = input,
                                chatID = currentDate,
                                senderID = currentDate,
                                receiverID = "gpt"
                            )
                        } else {
                            currentDate = viewModel.chatID.value
                            viewModel.askQuestion(
                                question = input,
                                chatID = currentDate!!,
                                senderID = currentDate,
                                receiverID = "gpt"
                            )
                            viewModel.loadMessages()
                        }
                        setInput("")

                        /* val currentDate = sdf.format(Date())

                         viewModel.changeDate(currentDate)
                         val date = viewModel.date.value

                         viewModel.askQuestion(
                             question = input,
                             chatID = "$date gpt",
                             senderID = date,
                             receiverID = "gpt"
                         )
                         setInput("") */
                    }
                },
            )
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
//        argument = null,
//        chatID = null
    )
}