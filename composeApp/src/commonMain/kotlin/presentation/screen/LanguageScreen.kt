import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import database.UserEntity
import viewmodel.LocalizationViewModel

@Composable
fun BoxWithConstraintsScope.LanguageContent(
    navController: NavController,
    localizationViewModel: LocalizationViewModel,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().width((maxWidth / 100 * 70)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val languages = AvailableLanguages.values().asList()
        items(languages.size) {
            Row {
                Button(onClick = {
                    localizationViewModel.setLang(UserEntity(lang = languages[it].toString())).apply {
                        navController.popBackStack()
                    }
                }) { Text(languages[it].toString()) }
            }
        }
    }
}
