package repositories

import androidx.compose.runtime.MutableState

interface UserInterface {

    fun signOut()
    fun signIn(email: String, password: String)
    fun signUp(email: String, password: String)
    fun reloadUser()
    fun sendEmailVerification()
    fun startPhoneNumberVerification(phoneNumber: String)
    fun resendPhoneNumberVerificationCode(phoneNumber: String)
    fun verifyPhoneNumberWithCode(code: String)

    fun userID(): String?
    fun email(): String?
    fun phoneNumber(): String?
    fun userName(): String?
    fun photoUrl(): String?
    fun isEmailVerified(): Boolean?

    val isPhoneCodeSent: MutableState<Boolean>
    val isEmailVerificationSent: MutableState<Boolean>
    val isEmailForgotPasswordSent: MutableState<Boolean>

    fun updateDisplayName(name: String)
    fun updateEmail(mail: String)
    fun updatePhotoLink(link: String)
    fun sendPasswordReset(email: String)
    fun updatePassword(password: String)
}