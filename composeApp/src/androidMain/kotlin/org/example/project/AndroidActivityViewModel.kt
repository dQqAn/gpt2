package org.example.project

import android.app.Activity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class AndroidActivityViewModel() : ViewModel(), KoinComponent {
    val activity: MutableState<Activity?> = mutableStateOf(null)
}