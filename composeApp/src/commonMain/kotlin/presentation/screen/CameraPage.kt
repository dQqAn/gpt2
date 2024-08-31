import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import util.Localization
import viewmodel.CameraViewModel

@Composable
expect fun CameraPage(
    navController: NavController,
    localization: Localization,
    cameraViewModel: CameraViewModel = viewModel()
)