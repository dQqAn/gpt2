package presentation.screen

import BluePrimary
import LoginViewModel
import MessageViewModel
import Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import viewmodel.MessageToChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageScreen(
    navController: NavController,
    viewModel: MessageViewModel = viewModel(),
    sharedVM: MessageToChatViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel()
//    gpt2Client: GPT2Client = viewModel()
) {
    val messages by viewModel.messages.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {

        },
        floatingActionButton = {

        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                /*Button(onClick = {
                    gpt2Client.launchAutocomplete()
                }) {
                    Text("Gpt2 Test")
                }*/
                Button(onClick = {
                    loginViewModel.signOut()
                    navController.navigate(route = Screen.SignIn.route)
                }) {
                    Text("Sign Out")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    onClick = {
                        navController.navigate(route = Screen.Chat.route) {
                            popUpTo(Screen.Chat.route) {
                                inclusive = true
                            }
                        }.apply {
                            val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS")
                            val currentDate = sdf.format(Date())
                            sharedVM.changeChatID("$currentDate gpt")
                            sharedVM.changeIsNewChat(true)
                        }
                    }) {
                    Text(modifier = Modifier, text = "New chat")
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
                horizontalAlignment = Alignment.End
            ) {
                items(messages.size) { index ->
                    val message = messages[index]
                    Row(
                        modifier = Modifier.clickable(onClick = {
                            navController.navigate(route = Screen.Chat.route) {
                                popUpTo(Screen.Chat.route) {
                                    inclusive = true
                                }
                            }.apply {
                                sharedVM.changeChatID(message)
                                sharedVM.changeIsNewChat(false)
                            }
                        }).fillMaxWidth()
                            .background(color = Color.Gray)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        message?.let {
                            Text(text = message)
                        }
                    }
                }
            }
        }
    }
}