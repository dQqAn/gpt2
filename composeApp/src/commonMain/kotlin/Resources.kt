import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun painterResource(id: Int): Painter

object MppR {
    object string {}

    object drawable {}

    object plurals {}
}

// Drawables
expect val MppR.drawable.ic_launcher: Int