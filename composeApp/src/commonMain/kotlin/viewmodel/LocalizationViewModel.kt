package viewmodel

import AvailableLanguages
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import database.UserDao
import database.UserDatabase
import database.UserEntity
import getDeviceLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocalizationViewModel : ViewModel(), UserDao, KoinComponent {
    private val database: UserDatabase by inject()

    private val _appLanguage: MutableState<AvailableLanguages?> = mutableStateOf(null)
    internal val appLanguage = _appLanguage
    private fun changeAppLanguage(userEntity: UserEntity?) {
        userEntity?.let { _appLanguage.value = AvailableLanguages.valueOf(it.lang) }
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val currentLang = getLang()
                if (currentLang.isEmpty()) {
                    changeAppLanguage(UserEntity(lang = getDeviceLanguage().name))
                } else {
                    changeAppLanguage(currentLang.last())
                }
            }
        }
    }

    override suspend fun getLang(): List<UserEntity?> = withContext(Dispatchers.Main) {
        database.userDao().getLang()
    }

    override fun setLang(userEntity: UserEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.userDao().setLang(userEntity)
                changeAppLanguage(userEntity)
            }
        }
    }

    override suspend fun clear() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.userDao().clear()
            }
        }
    }
}

