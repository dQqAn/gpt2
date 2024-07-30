import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LoginViewModel : ViewModel(), KoinComponent, UserRepository.ScreenListener {
    private val repository: UserRepository by inject() {
        parametersOf(this as UserRepository.ScreenListener)
    }

    private val _navController: MutableState<NavController?> = mutableStateOf(null)
    internal fun setNavController(navController: NavController) {
        _navController.value = navController
    }

    private val _signInMailText = mutableStateOf("")
    internal val signInMailText = _signInMailText

    internal fun signInMailTextSetName(name: String) {
        _signInMailText.value = name
    }

    private val _signInPasswordText = mutableStateOf("")
    internal val signInPasswordText = _signInPasswordText

    internal fun signInPasswordTextSetName(name: String) {
        _signInPasswordText.value = name
    }


    private val _signUpFirstMailText = mutableStateOf("")
    internal val signUpFirstMailText = _signUpFirstMailText

    internal fun signUpFirstMailTextSetName(name: String) {
        _signUpFirstMailText.value = name
    }

    private val _signUpSecondMailText = mutableStateOf("")
    internal val signUpSecondMailText = _signUpSecondMailText

    internal fun signUpSecondMailTextSetName(name: String) {
        _signUpSecondMailText.value = name
    }

    private val _signUpFirstPasswordText = mutableStateOf("")
    internal val signUpFirstPasswordText = _signUpFirstPasswordText

    internal fun signUpFirstPasswordTextSetName(name: String) {
        _signUpFirstPasswordText.value = name
    }

    private val _signUpSecondPasswordText = mutableStateOf("")
    internal val signUpSecondPasswordText = _signUpSecondPasswordText

    internal fun signUpSecondPasswordTextSetName(name: String) {
        _signUpSecondPasswordText.value = name
    }


    private val _phoneAreaCodeText = mutableStateOf("")
    internal val phoneAreaCodeText = _phoneAreaCodeText

    internal fun changePhoneAreaCodeTextSetName(name: String) {
        _phoneAreaCodeText.value = name
    }

    private val _phoneNumberText = mutableStateOf("")
    internal val phoneNumberText = _phoneNumberText

    internal fun changePhoneNumberTextSetName(name: String) {
        _phoneNumberText.value = name
    }

    private val _phoneVerificationCodeText = mutableStateOf("")
    internal val phoneVerificationCodeText = _phoneVerificationCodeText

    internal fun changePhoneVerificationCodeTextSetName(name: String) {
        _phoneVerificationCodeText.value = name
    }


    internal fun signIn() {
        if (signInMailText.value.isNotEmpty() && signInPasswordText.value.isNotEmpty()) {
            repository.signIn(signInMailText.value, signInPasswordText.value)

        } else {
//            repository.showShortToastMessage(currentLocalizationValue.enterYourEmailAndPassword)
        }
    }

    internal fun signUp() {
        if (signUpFirstMailText.value != signUpSecondMailText.value) {
//            repository.showShortToastMessage(currentLocalizationValue.yourEmailsAreNotSame)
        } else if (signUpFirstPasswordText.value != signUpSecondPasswordText.value) {
//            repository.showShortToastMessage(currentLocalizationValue.yourPasswordsAreNotSame)
        } else {
            if (signUpFirstMailText.value.isNotEmpty() && signUpFirstPasswordText.value.isNotEmpty()) {
                repository.signUp(signUpFirstMailText.value, signUpFirstPasswordText.value)
            } else {
//                repository.showShortToastMessage(currentLocalizationValue.enterYourEmailAndPassword)
            }
        }
    }

    internal fun sendEmailVerification() = repository.sendEmailVerification()
    internal fun forgotPassword() {
        if (signInMailText.value.isNotEmpty()) {
            repository.sendPasswordReset(signInMailText.value)
        } else {
//            repository.showShortToastMessage(currentLocalizationValue.enterYourInformation)
        }
    }

    internal fun email(): String? = repository.email()
    internal fun isEmailVerified(): Boolean? = repository.isEmailVerified()
    private fun phoneNumber(): String? = repository.phoneNumber()
    internal fun signOut() = repository.signOut()
    internal fun reloadUser() {
        viewModelScope.launch(Dispatchers.Main) {
            repository.reloadUser()
        }
    }

    internal fun isPhoneCodeSent(): MutableState<Boolean> = repository.isPhoneCodeSent
    internal fun changeIsPhoneCodeSent(initialize: Boolean) {
        repository.isPhoneCodeSent.value = initialize
    }

    internal fun isEmailVerificationSent(): MutableState<Boolean> = repository.isEmailVerificationSent
    internal fun changeIsEmailVerificationSent(initialize: Boolean) {
        repository.isEmailVerificationSent.value = initialize
    }

    internal fun isEmailForgotPasswordSent(): MutableState<Boolean> = repository.isEmailForgotPasswordSent
    internal fun changeIsEmailForgotPasswordSent(initialize: Boolean) {
        repository.isEmailForgotPasswordSent.value = initialize
    }

    internal fun getPage(): Screen {
        reloadUser()

        val isEmailVerified: Boolean? = isEmailVerified()
        val phoneNumberStatus: String? = phoneNumber()
        val userId: String? = repository.userID()

        return if (isEmailVerified == true && phoneNumberStatus != null) {
            Screen.Message
        } else if (isEmailVerified == true && phoneNumberStatus == null) {
            Screen.PhoneVerification
        } else if (isEmailVerified == false && userId != null) {
            Screen.MailVerification
        } else {
            Screen.SignIn
        }
    }

    internal fun startPhoneNumberVerification() {
        if (_phoneAreaCodeText.value.isNotEmpty() && _phoneNumberText.value.isNotEmpty()) {
            val phoneNumberWithAreaCode = mutableStateOf("+" + _phoneAreaCodeText.value + _phoneNumberText.value)
            repository.startPhoneNumberVerification(phoneNumberWithAreaCode.value)
        } else {
//            repository.showShortToastMessage(currentLocalizationValue.enterYourInformation)
        }
    }

    internal fun resendPhoneNumberVerificationCode() {
        if (_phoneAreaCodeText.value.isNotEmpty() && _phoneNumberText.value.isNotEmpty()) {
            val phoneNumberWithAreaCode = mutableStateOf("+" + _phoneAreaCodeText.value + _phoneNumberText.value)
            repository.resendPhoneNumberVerificationCode(phoneNumberWithAreaCode.value)
        } else {
//            repository.showShortToastMessage(currentLocalizationValue.enterYourInformation)
        }
    }

    internal fun verifyPhoneNumberWithCode() {
        if (_phoneVerificationCodeText.value.isNotEmpty()) {
            repository.verifyPhoneNumberWithCode(_phoneVerificationCodeText.value)
        } else {
//            repository.showShortToastMessage(currentLocalizationValue.checkYourVerificationCode)
        }
    }


    private val _columnState = mutableStateOf(true)
    internal val columnState = _columnState

    internal fun changeColumnState(state: Boolean) {
        _columnState.value = state
    }

    override fun onError(error: String) {
        println(error)
    }

    override fun onResults(screen: Screen) {
        _navController.value?.navigate(route = screen.route) {
            popUpTo(screen.route) {
                inclusive = true
            }
        }
    }
}
