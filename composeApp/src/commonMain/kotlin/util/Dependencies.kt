package util

interface Localization {

    //login texts
    val yourMail: String
    val confirmYourMail: String
    val yourPassword: String
    val confirmYourPassword: String
    val signIn: String
    val forgotPassword: String
    val createNewAccount: String
    val next: String
    val back: String
    val youAlreadyHaveAnAccount: String
    val mailVerificationCode: String
    val phoneVerificationCode: String
    val send: String
    val resend: String
    val checkYourMail: String
    val checkYourPhone: String
    val checkYourEmailAndSignIn: String
    val selectYourLanguage: String

    //icons description
    val showPassword: String
    val hidePassword: String
    val mailIcon: String
    val clearIcon: String
    val passwordIcon: String
    val visibilityIcon: String
    val verificationCodeIcon: String
    val areaCodeIcon: String
    val phoneNumberIcon: String

    //authentication
    val authenticationFailed: String
    val emailVerificationFailed: String
    val verificationCodeSent: String
    val loggedInAs: String
    val userNameUpdated: String
    val userEmailAddressUpdated: String
    val userPhotoUpdated: String
    val emailSent: String
    val userPasswordUpdated: String
    val yourEmailsAreNotSame: String
    val yourPasswordsAreNotSame: String
    val enterYourEmailAndPassword: String
    val areaCode: String
    val phoneNumber: String
    val checkYourVerificationCode: String
    val enterYourInformation: String
}
