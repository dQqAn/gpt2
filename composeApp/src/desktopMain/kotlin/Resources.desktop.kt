import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
actual fun painterResource(id: Int): Painter {
    return androidx.compose.ui.res.painterResource(id.toString())
}

actual val MppR.drawable.ic_launcher: Int
    get() = 0