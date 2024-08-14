import androidx.compose.ui.text.intl.Locale

actual fun getDeviceLanguage(): AvailableLanguages {
    val currentLanguage = Locale.current.language.uppercase()
    var getLang = AvailableLanguages.values().find { it.name == currentLanguage }.toString()
    getLang = getLang.ifEmpty { "EN" }
    return AvailableLanguages.valueOf(getLang)
}
