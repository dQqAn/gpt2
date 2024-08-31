package viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class CameraViewModel : ViewModel(), KoinComponent {

    internal val filterNumber = mutableStateOf(2)
    internal val filterActive = mutableStateOf(false)
    internal val filteredBitmap: MutableState<Bitmap?> = mutableStateOf(null)
}