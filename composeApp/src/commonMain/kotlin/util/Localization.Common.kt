import util.Localization
import viewmodel.LocalizationViewModel

enum class AvailableLanguages { DE, EN, TR, FR, RU; }

expect fun getDeviceLanguage(): AvailableLanguages

fun getCurrentLocalization(localizationViewModel: LocalizationViewModel) =
    when (localizationViewModel.appLanguage.value) {
        // var AppLanguage: AvailableLanguages = getDeviceLanguage()
        AvailableLanguages.EN -> EnglishLocalization
        AvailableLanguages.DE -> DeutschLocalization
        AvailableLanguages.TR -> TurkishLocalization
        AvailableLanguages.FR -> FrenchLocalization
        AvailableLanguages.RU -> RussianLocalization
        else -> {
            null
        }
    }

object EnglishLocalization : Localization {
    override val yourMail: String
        get() = "Your Email"
    override val confirmYourMail: String
        get() = "Confirm Your Email"
    override val yourPassword: String
        get() = "Your Password"
    override val confirmYourPassword: String
        get() = "Confirm Your Password"
    override val signIn: String
        get() = "Sign In"
    override val forgotPassword: String
        get() = "Forgot Password"
    override val createNewAccount: String
        get() = "Create New Account"
    override val next: String
        get() = "Next"
    override val back: String
        get() = "Back"
    override val youAlreadyHaveAnAccount: String
        get() = "Do you already have an account?"
    override val mailVerificationCode: String
        get() = "E-mail verification code"
    override val phoneVerificationCode: String
        get() = "Phone verification code"
    override val send: String
        get() = "Send"
    override val resend: String
        get() = "Resend"
    override val checkYourMail: String
        get() = "Check your e-mail"
    override val checkYourPhone: String
        get() = "Check your phone"
    override val checkYourEmailAndSignIn: String
        get() = "Check your email and sign in"
    override val selectYourLanguage: String
        get() = "Select Your Language"
    override val showPassword: String
        get() = "Show Password"
    override val hidePassword: String
        get() = "Hide Password"
    override val mailIcon: String
        get() = "Mail Icon"
    override val clearIcon: String
        get() = "Clear Icon"
    override val passwordIcon: String
        get() = "Password Icon"
    override val visibilityIcon: String
        get() = "Visibility Icon"
    override val verificationCodeIcon: String
        get() = "Verification Code Icon"
    override val areaCodeIcon: String
        get() = "Area Code Icon"
    override val phoneNumberIcon: String
        get() = "Phone Number Icon"
    override val authenticationFailed: String
        get() = "Authentication Failed"
    override val emailVerificationFailed: String
        get() = "Email Verification Failed"
    override val verificationCodeSent: String
        get() = "Verification Code Sent"
    override val loggedInAs: String
        get() = "Logged In As"
    override val userNameUpdated: String
        get() = "User Name Updated"
    override val userEmailAddressUpdated: String
        get() = "User Email Address Updated"
    override val userPhotoUpdated: String
        get() = "User Photo Updated"
    override val emailSent: String
        get() = "E-mail Sent"
    override val userPasswordUpdated: String
        get() = "User Password Updated"
    override val yourEmailsAreNotSame: String
        get() = "Your emails are not same."
    override val yourPasswordsAreNotSame: String
        get() = "Your passwords are not same."
    override val enterYourEmailAndPassword: String
        get() = "Enter your email and password."
    override val areaCode: String
        get() = "Area Code"
    override val phoneNumber: String
        get() = "Phone Number"
    override val checkYourVerificationCode: String
        get() = "Check your verification code."
    override val enterYourInformation: String
        get() = "Enter your information."

}

object DeutschLocalization : Localization {
    override val yourMail: String
        get() = "Ihre E-Mail"
    override val confirmYourMail: String
        get() = "Bestätigen Sie Ihre E-Mail"
    override val yourPassword: String
        get() = "Ihr Passwort"
    override val confirmYourPassword: String
        get() = "Bestätigen Sie Ihr Passwort"
    override val signIn: String
        get() = "Anmelden"
    override val forgotPassword: String
        get() = "Passwort vergessen"
    override val createNewAccount: String
        get() = "Neues Konto erstellen"
    override val next: String
        get() = "Weiter"
    override val back: String
        get() = "Zurück"
    override val youAlreadyHaveAnAccount: String
        get() = "Haben Sie bereits ein Konto?"
    override val mailVerificationCode: String
        get() = "E-Mail-Verifizierungscode"
    override val phoneVerificationCode: String
        get() = "Telefon-Verifizierungscode"
    override val send: String
        get() = "Senden"
    override val resend: String
        get() = "Erneut senden"
    override val checkYourMail: String
        get() = "Überprüfen Sie Ihre E-Mail"
    override val checkYourPhone: String
        get() = "Überprüfen Sie Ihr Telefon"
    override val checkYourEmailAndSignIn: String
        get() = "Überprüfen Sie Ihre E-Mail und melden Sie sich an"
    override val selectYourLanguage: String
        get() = "Wählen Sie Ihre Sprache"
    override val showPassword: String
        get() = "Passwort anzeigen"
    override val hidePassword: String
        get() = "Passwort ausblenden"
    override val mailIcon: String
        get() = "Briefsymbol"
    override val clearIcon: String
        get() = "Lösen-Symbol"
    override val passwordIcon: String
        get() = "Passwortsymbol"
    override val visibilityIcon: String
        get() = "Sichtbarkeitssymbol"
    override val verificationCodeIcon: String
        get() = "Verifizierungscode-Symbol"
    override val areaCodeIcon: String
        get() = "Vorwahl-Symbol"
    override val phoneNumberIcon: String
        get() = "Telefonnummern-Symbol"
    override val authenticationFailed: String
        get() = "Authentifizierung fehlgeschlagen"
    override val emailVerificationFailed: String
        get() = "E-Mail-Verifizierung fehlgeschlagen"
    override val verificationCodeSent: String
        get() = "Verifizierungscode gesendet"
    override val loggedInAs: String
        get() = "Angemeldet als"
    override val userNameUpdated: String
        get() = "Benutzername aktualisiert"
    override val userEmailAddressUpdated: String
        get() = "Benutzer-E-Mail-Adresse aktualisiert"
    override val userPhotoUpdated: String
        get() = "Benutzerfoto aktualisiert"
    override val emailSent: String
        get() = "E-Mail gesendet"
    override val userPasswordUpdated: String
        get() = "Benutzerpasswort aktualisiert"
    override val yourEmailsAreNotSame: String
        get() = "Ihre E-Mails sind nicht identisch."
    override val yourPasswordsAreNotSame: String
        get() = "Ihre Passwörter sind nicht identisch."
    override val enterYourEmailAndPassword: String
        get() = "Geben Sie Ihre E-Mail und Ihr Passwort ein."
    override val areaCode: String
        get() = "Vorwahl"
    override val phoneNumber: String
        get() = "Telefonnummer"
    override val checkYourVerificationCode: String
        get() = "Überprüfen Sie Ihren Verifizierungscode."
    override val enterYourInformation: String
        get() = "Geben Sie Ihre Informationen ein."
}

object TurkishLocalization : Localization {
    override val yourMail: String
        get() = "E-postanız"
    override val confirmYourMail: String
        get() = "E-postanızı Onaylayın"
    override val yourPassword: String
        get() = "Şifreniz"
    override val confirmYourPassword: String
        get() = "Şifrenizi Onaylayın"
    override val signIn: String
        get() = "Giriş Yap"
    override val forgotPassword: String
        get() = "Şifremi Unuttum"
    override val createNewAccount: String
        get() = "Yeni Hesap Oluştur"
    override val next: String
        get() = "Sonraki"
    override val back: String
        get() = "Geri"
    override val youAlreadyHaveAnAccount: String
        get() = "Zaten bir hesabınız var mı?"
    override val mailVerificationCode: String
        get() = "E-posta Doğrulama Kodu"
    override val phoneVerificationCode: String
        get() = "Telefon Doğrulama Kodu"
    override val send: String
        get() = "Gönder"
    override val resend: String
        get() = "Tekrar Gönder"
    override val checkYourMail: String
        get() = "E-postanızı kontrol edin"
    override val checkYourPhone: String
        get() = "Telefonunuzu kontrol edin"
    override val checkYourEmailAndSignIn: String
        get() = "E-postanızı kontrol edin ve giriş yapın"
    override val selectYourLanguage: String
        get() = "Dilinizi Seçin"
    override val showPassword: String
        get() = "Şifreyi Göster"
    override val hidePassword: String
        get() = "Şifreyi Gizle"
    override val mailIcon: String
        get() = "E-posta Simgesi"
    override val clearIcon: String
        get() = "Temizle Simgesi"
    override val passwordIcon: String
        get() = "Şifre Simgesi"
    override val visibilityIcon: String
        get() = "Görünürlük Simgesi"
    override val verificationCodeIcon: String
        get() = "Doğrulama Kodu Simgesi"
    override val areaCodeIcon: String
        get() = "Alan Kodu Simgesi"
    override val phoneNumberIcon: String
        get() = "Telefon Numarası Simgesi"
    override val authenticationFailed: String
        get() = "Kimlik Doğrulama Başarısız Oldu"
    override val emailVerificationFailed: String
        get() = "E-posta Doğrulama Başarısız Oldu"
    override val verificationCodeSent: String
        get() = "Doğrulama Kodu Gönderildi"
    override val loggedInAs: String
        get() = "Şu Kişi Olarak Giriş Yapıldı: "
    override val userNameUpdated: String
        get() = "Kullanıcı Adı Güncellendi"
    override val userEmailAddressUpdated: String
        get() = "Kullanıcı E-posta Adresi Güncellendi"
    override val userPhotoUpdated: String
        get() = "Kullanıcı Fotoğrafı Güncellendi"
    override val emailSent: String
        get() = "E-posta Gönderildi"
    override val userPasswordUpdated: String
        get() = "Kullanıcı Şifresi Güncellendi"
    override val yourEmailsAreNotSame: String
        get() = "E-postalarınız aynı değil."
    override val yourPasswordsAreNotSame: String
        get() = "Şifreleriniz aynı değil."
    override val enterYourEmailAndPassword: String
        get() = "E-posta ve şifrenizi girin."
    override val areaCode: String
        get() = "Alan Kodu"
    override val phoneNumber: String
        get() = "Telefon Numarası"
    override val checkYourVerificationCode: String
        get() = "Doğrulama kodunuzu kontrol edin."
    override val enterYourInformation: String
        get() = "Bilgilerinizi girin."
}

object FrenchLocalization : Localization {
    override val yourMail: String
        get() = "Votre adresse e-mail"
    override val confirmYourMail: String
        get() = "Confirmez votre adresse e-mail"
    override val yourPassword: String
        get() = "Votre mot de passe"
    override val confirmYourPassword: String
        get() = "Confirmez votre mot de passe"
    override val signIn: String
        get() = "Se connecter"
    override val forgotPassword: String
        get() = "Mot de passe oublié"
    override val createNewAccount: String
        get() = "Créer un nouveau compte"
    override val next: String
        get() = "Suivant"
    override val back: String
        get() = "Retour"
    override val youAlreadyHaveAnAccount: String
        get() = "Avez-vous déjà un compte ?"
    override val mailVerificationCode: String
        get() = "Code de vérification par e-mail"
    override val phoneVerificationCode: String
        get() = "Code de vérification par téléphone"
    override val send: String
        get() = "Envoyer"
    override val resend: String
        get() = "Renvoyer"
    override val checkYourMail: String
        get() = "Vérifiez vos e-mails"
    override val checkYourPhone: String
        get() = "Vérifiez votre téléphone"
    override val checkYourEmailAndSignIn: String
        get() = "Vérifiez votre e-mail et connectez-vous"
    override val selectYourLanguage: String
        get() = "Sélectionnez votre langue"
    override val showPassword: String
        get() = "Afficher le mot de passe"
    override val hidePassword: String
        get() = "Masquer le mot de passe"
    override val mailIcon: String
        get() = "Icône de messagerie"
    override val clearIcon: String
        get() = "Icône de suppression"
    override val passwordIcon: String
        get() = "Icône de mot de passe"
    override val visibilityIcon: String
        get() = "Icône de visibilité"
    override val verificationCodeIcon: String
        get() = "Icône de code de vérification"
    override val areaCodeIcon: String
        get() = "Icône de code régional"
    override val phoneNumberIcon: String
        get() = "Icône de numéro de téléphone"
    override val authenticationFailed: String
        get() = "L'authentification a échoué"
    override val emailVerificationFailed: String
        get() = "La vérification de l'e-mail a échoué"
    override val verificationCodeSent: String
        get() = "Code de vérification envoyé"
    override val loggedInAs: String
        get() = "Connecté en tant que"
    override val userNameUpdated: String
        get() = "Nom d'utilisateur mis à jour"
    override val userEmailAddressUpdated: String
        get() = "Adresse e-mail de l'utilisateur mise à jour"
    override val userPhotoUpdated: String
        get() = "Photo de l'utilisateur mise à jour"
    override val emailSent: String
        get() = "E-mail envoyé"
    override val userPasswordUpdated: String
        get() = "Mot de passe de l'utilisateur mis à jour"
    override val yourEmailsAreNotSame: String
        get() = "Vos adresses e-mail ne correspondent pas."
    override val yourPasswordsAreNotSame: String
        get() = "Vos mots de passe ne correspondent pas."
    override val enterYourEmailAndPassword: String
        get() = "Entrez votre adresse e-mail et votre mot de passe."
    override val areaCode: String
        get() = "Code régional"
    override val phoneNumber: String
        get() = "Numéro de téléphone"
    override val checkYourVerificationCode: String
        get() = "Vérifiez votre code de vérification."
    override val enterYourInformation: String
        get() = "Entrez vos informations."
}

object RussianLocalization : Localization {
    override val yourMail: String
        get() = "Ваш e-mail"
    override val confirmYourMail: String
        get() = "Подтвердите ваш e-mail"
    override val yourPassword: String
        get() = "Ваш пароль"
    override val confirmYourPassword: String
        get() = "Подтвердите ваш пароль"
    override val signIn: String
        get() = "Войти"
    override val forgotPassword: String
        get() = "Забыли пароль"
    override val createNewAccount: String
        get() = "Создать новый аккаунт"
    override val next: String
        get() = "Далее"
    override val back: String
        get() = "Назад"
    override val youAlreadyHaveAnAccount: String
        get() = "У вас уже есть аккаунт?"
    override val mailVerificationCode: String
        get() = "Код подтверждения по e-mail"
    override val phoneVerificationCode: String
        get() = "Код подтверждения по телефону"
    override val send: String
        get() = "Отправить"
    override val resend: String
        get() = "Отправить повторно"
    override val checkYourMail: String
        get() = "Проверьте ваш e-mail"
    override val checkYourPhone: String
        get() = "Проверьте ваш телефон"
    override val checkYourEmailAndSignIn: String
        get() = "Проверьте ваш e-mail и войдите"
    override val selectYourLanguage: String
        get() = "Выберите ваш язык"
    override val showPassword: String
        get() = "Показать пароль"
    override val hidePassword: String
        get() = "Скрыть пароль"
    override val mailIcon: String
        get() = "Иконка почты"
    override val clearIcon: String
        get() = "Иконка очистки"
    override val passwordIcon: String
        get() = "Иконка пароля"
    override val visibilityIcon: String
        get() = "Иконка видимости"
    override val verificationCodeIcon: String
        get() = "Иконка кода подтверждения"
    override val areaCodeIcon: String
        get() = "Иконка кода региона"
    override val phoneNumberIcon: String
        get() = "Иконка номера телефона"
    override val authenticationFailed: String
        get() = "Ошибка аутентификации"
    override val emailVerificationFailed: String
        get() = "Ошибка подтверждения e-mail"
    override val verificationCodeSent: String
        get() = "Код подтверждения отправлен"
    override val loggedInAs: String
        get() = "Вы вошли как"
    override val userNameUpdated: String
        get() = "Имя пользователя обновлено"
    override val userEmailAddressUpdated: String
        get() = "Адрес e-mail пользователя обновлен"
    override val userPhotoUpdated: String
        get() = "Фото пользователя обновлено"
    override val emailSent: String
        get() = "E-mail отправлен"
    override val userPasswordUpdated: String
        get() = "Пароль пользователя обновлен"
    override val yourEmailsAreNotSame: String
        get() = "Ваши e-mail не совпадают."
    override val yourPasswordsAreNotSame: String
        get() = "Ваши пароли не совпадают."
    override val enterYourEmailAndPassword: String
        get() = "Введите ваш e-mail и пароль."
    override val areaCode: String
        get() = "Код региона"
    override val phoneNumber: String
        get() = "Номер телефона"
    override val checkYourVerificationCode: String
        get() = "Проверьте ваш код подтверждения."
    override val enterYourInformation: String
        get() = "Введите вашу информацию."
}
