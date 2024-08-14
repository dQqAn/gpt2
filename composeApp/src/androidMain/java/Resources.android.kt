import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.example.project.R

@Composable
actual fun painterResource(id: Int): Painter {
    return androidx.compose.ui.res.painterResource(id)
}

actual val MppR.drawable.ic_launcher: Int
    get() = R.drawable.ic_launcher_background