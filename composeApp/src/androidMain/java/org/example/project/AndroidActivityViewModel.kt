package org.example.project

import android.app.Activity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AndroidActivityViewModel() : ViewModel() {
    val activity: MutableState<Activity?> = mutableStateOf(null)
}