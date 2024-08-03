import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import org.example.project.AndroidActivityViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.components.UserInterface
import java.util.concurrent.TimeUnit

actual class UserRepository(
    private val screenListener: ScreenListener
) : UserInterface, KoinComponent {

//    private val screenListener: ScreenListener by inject()

    private val context: Context by inject()
    private val androidActivityViewModel: AndroidActivityViewModel by inject()
    private val activity = androidActivityViewModel.activity.value as Activity

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val auth : FirebaseAuth = Firebase.auth

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override val isPhoneCodeSent: MutableState<Boolean> = mutableStateOf(false)
    override val isEmailVerificationSent: MutableState<Boolean> = mutableStateOf(false)
    override val isEmailForgotPasswordSent: MutableState<Boolean> = mutableStateOf(false)

    override fun signOut() {
        auth.currentUser?.let { auth.signOut() }
    }

    private fun getScreen(): Screen {
        val isEmailVerified: Boolean? = isEmailVerified()
        val phoneNumberStatus: String? = phoneNumber()
        val userId: String? = userID()

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

    override fun signIn(email: String, password: String) {
        signOut()
        if (auth.currentUser == null) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.v("Firebase", "signInWithEmail: success")
                        screenListener.onResults(getScreen())
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.v("Firebase", "signInWithEmail: failure", task.exception)
                        screenListener.onError("signInWithEmail: failure, " + task.exception)
                    }
                }
        } else {

        }
    }

    override fun signUp(email: String, password: String) {
        signOut()
        if (auth.currentUser == null) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.v("Firebase", "createUserWithEmail:success")
                        screenListener.onResults(Screen.MailVerification)
                        auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.v("Firebase", "createUserWithEmail:failure", task.exception)
                        screenListener.onError("signInWithEmail: failure, " + task.exception)
                    }
                }
        } else {

        }
    }

    override fun sendEmailVerification() {
        auth.currentUser?.let {
            it.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.v("Firebase", "sendEmailVerification: success")
                        isEmailVerificationSent.value = true
                    } else {
                        Log.v("Firebase", "sendEmailVerification: failure", task.exception)
//                        showShortToastMessage(localization.emailVerificationFailed)
                    }
                }
        }
    }

    override fun startPhoneNumberVerification(phoneNumber: String) {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.v("Firebase", "onVerificationCompleted:$credential")
                linkWithPhoneAuthCredential(credential) //                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the phone number format is not valid.
//                Log.v("Firebase", "onVerificationFailed", e)

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> { // Invalid request
//                        showShortToastMessage(e.message.toString())
                        Log.v("Firebase", e.message!!)
                    }

                    is FirebaseTooManyRequestsException -> { // The SMS quota for the project has been exceeded
//                        showShortToastMessage(e.message.toString())
                        Log.v("Firebase", e.message!!)
                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> { // re CAPTCHA verification attempted with null Activity
//                        showShortToastMessage(e.message.toString())
                        Log.v("Firebase", e.message!!)
                    }

                    else -> {
//                        showShortToastMessage(e.message.toString())
                        Log.v("Firebase", e.message!!)
                    }
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.v("Firebase", "onCodeSent:$verificationId")

                // Save verification ID and resending token, so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                isPhoneCodeSent.value = true

//                showShortToastMessage(localization.verificationCodeSent)
            }
        }

        callbacks?.let {
            auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true).apply {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(activity) // Activity (for callback binding)
                    .setCallbacks(it) // OnVerificationStateChangedCallbacks
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }

    override fun resendPhoneNumberVerificationCode(phoneNumber: String) {
        if (callbacks != null && resendToken != null) {
            val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(activity) // (optional) Activity for callback binding
                // If no activity is passed, reCAPTCHA verification can not be used.
                .setCallbacks(callbacks!!) // OnVerificationStateChangedCallbacks

            Log.v("Firebase", "resendToken:$resendToken")

            optionsBuilder.setForceResendingToken(resendToken!!) //                optionsBuilder.setForceResendingToken(token as PhoneAuthProvider.ForceResendingToken) // callback's ForceResendingToken
            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
        }
    }

    override fun verifyPhoneNumberWithCode(code: String) {
        storedVerificationId?.let {
            Log.v("Firebase", "storedVerificationId:$it")
            val credential = PhoneAuthProvider.getCredential(it, code)
            linkWithPhoneAuthCredential(credential) //        signInWithPhoneAuthCredential(credential)
        }
    }

    override fun linkWithPhoneAuthCredential(credential: Any?) {
        auth.currentUser?.let {
            it.linkWithCredential(credential as PhoneAuthCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        showShortToastMessage(localization.loggedInAs + " " + it.phoneNumber)
                        screenListener.onResults(Screen.Message)
                    } else {
                        screenListener.onError("linkWithCredential: failure, " + task.exception)
                    }
                }
        }
    }

    override fun userID(): String? {
        return auth.currentUser?.uid //Do NOT use this value to authenticate with your backend server, if you have one. Use FirebaseUser.getIdToken() instead.
    }

    override fun email(): String? {
        return auth.currentUser?.email
    }

    override fun phoneNumber(): String? {
        return auth.currentUser?.phoneNumber
    }

    override fun userName(): String? {
        return auth.currentUser?.displayName
    }

    override fun photoUrl(): String? {
        return auth.currentUser?.photoUrl?.path //encodedPath
    }

    override fun isEmailVerified(): Boolean? {
        return auth.currentUser?.isEmailVerified
    }

    override suspend fun reloadUser() {
        try {
            auth.currentUser?.reload()
                ?.await() //            (auth.currentUser?.getIdToken(true)) //it's working in the second call
        } catch (e: Exception) {
            auth.signOut()
            Log.d("Firebase", "User may have been deleted.")
        }
    }

    override fun updateDisplayName(name: String) {
        auth.currentUser?.let {
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }

            it.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        Log.v("Firebase", localization.userNameUpdated)
                    }
                }
        }
    }

    override fun updateEmail(mail: String) {
        auth.currentUser?.let {
            it.updateEmail(mail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        Log.v("Firebase", localization.userEmailAddressUpdated)
                    }
                }
        }
    }

    override fun updatePhotoLink(link: String) {
        auth.currentUser?.let {
            val profileUpdates = userProfileChangeRequest {
                photoUri = Uri.parse(link)
            }

            it.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        Log.v("Firebase", localization.userPhotoUpdated)
                    }
                }
        }
    }

    override fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isEmailForgotPasswordSent.value = true
//                    Log.v("Firebase", localization.emailSent)
                }
            }
    }

    override fun updatePassword(password: String) {
        auth.currentUser?.let {
            it.updatePassword(password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        Log.v("Firebase", localization.userPasswordUpdated)
                    }
                }
        }
    }

    actual interface ScreenListener {
        actual fun onError(error: String)
        actual fun onResults(screen: Screen)
    }
}
