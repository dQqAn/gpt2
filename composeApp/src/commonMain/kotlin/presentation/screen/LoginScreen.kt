import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import gpt2.composeapp.generated.resources.Res


@Composable
fun SignInContent(
    loginViewModel: LoginViewModel,
    onSignUpPageClick: () -> Unit,
    onLocalizationPageClick: () -> Unit,
    onMessagePageClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    maxWidth: Dp,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        MyOutlinedTextField(
            text = loginViewModel.signInMailText.value,
            onTextChanged = loginViewModel::signInMailTextSetName,
            placeHolderText = "Your email",
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
            leadingDescription = "Email icon",
            trailingDescription = "Clear icon",
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        PasswordOutlinedTextField(
            text = loginViewModel.signInPasswordText.value,
            onTextChanged = loginViewModel::signInPasswordTextSetName,
            placeHolderText = "Your password",
            keyboardType = KeyboardType.Password,
            leadingIcon = Icons.Default.Lock,
            leadingDescription = "Password icon",
            trailingDescription = "Visibility icon",
            maxWidth
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = {
            loginViewModel.signIn()
        }) { Text("Sign in") }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(onClick = {
            onForgotPasswordClick()
        }) { Text("Forgot password") }

        Spacer(modifier = Modifier.height(14.dp))

        Button(onClick = {
            onSignUpPageClick()
        }) { Text("Create new account") }

        Spacer(modifier = Modifier.height(28.dp))

        TextButton(modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { onLocalizationPageClick() }) { Text(text = "Select your language") }
    }
}

@Composable
fun SignUpContent(
    loginViewModel: LoginViewModel,
    onNavigationClick: () -> Unit,
    onBackClick: () -> Unit,
    maxWidth: Dp,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        MyOutlinedTextField(
            text = loginViewModel.signUpFirstMailText.value,
            onTextChanged = loginViewModel::signUpFirstMailTextSetName,
            placeHolderText = "Your email",
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
            leadingDescription = "Email icon",
            trailingDescription = "Clear icon",
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        MyOutlinedTextField(
            text = loginViewModel.signUpSecondMailText.value,
            onTextChanged = loginViewModel::signUpSecondMailTextSetName,
            placeHolderText = "Confirm your email",
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
            leadingDescription = "Email icon",
            trailingDescription = "Clear icon",
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        PasswordOutlinedTextField(
            text = loginViewModel.signUpFirstPasswordText.value,
            onTextChanged = loginViewModel::signUpFirstPasswordTextSetName,
            placeHolderText = "Your password",
            keyboardType = KeyboardType.Password,
            leadingIcon = Icons.Default.Lock,
            leadingDescription = "Password icon",
            trailingDescription = "Visibility icon",
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        PasswordOutlinedTextField(
            text = loginViewModel.signUpSecondPasswordText.value,
            onTextChanged = loginViewModel::signUpSecondPasswordTextSetName,
            placeHolderText = "Confirm your password",
            keyboardType = KeyboardType.Password,
            leadingIcon = Icons.Default.Lock,
            leadingDescription = "Password icon",
            trailingDescription = "Visibility icon",
            maxWidth
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = {
            loginViewModel.signUp()
        }) { Text("Next") }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(onClick = {
            onBackClick()
        }) { Text("You already have account") }

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
fun MailVerificationContent(
    loginViewModel: LoginViewModel,
    onNavigationClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val isEmailSent by remember { loginViewModel.isEmailVerificationSent() }
    val checkEmail = loginViewModel.signUpFirstMailText.value.ifEmpty { loginViewModel.email() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        if (checkEmail != null) {
            Text(text = checkEmail)
        }

        if (isEmailSent) {
            Spacer(modifier = Modifier.height(14.dp))
            Text("Check your email")
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = {
            loginViewModel.sendEmailVerification()
        }) {
            if (!isEmailSent) {
                Text("Send")
            } else {
                Text("Resend")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row {
            TextButton(onClick = {
                loginViewModel.signOut().apply {
                    onBackClick()
                    loginViewModel.changeIsEmailVerificationSent(false)
                }
            }) { Text("Back") }

            Spacer(modifier = Modifier.width(28.dp))

            Button(onClick = {
                loginViewModel.reloadUser().apply {
                    if (loginViewModel.isEmailVerified() == true) {
                        onNavigationClick()
                    }
                }
            }) { Text("Next") }
        }
    }
}

@Composable
fun PhoneVerificationContent(
    loginViewModel: LoginViewModel,
    onNavigationClick: () -> Unit,
    onBackClick: () -> Unit,
    maxWidth: Dp,
) {
    val isCodeSent by remember { loginViewModel.isPhoneCodeSent() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        CountryCodePicker(loginViewModel, maxWidth) //phone area code text field

        if (loginViewModel.columnState.value) {

            Spacer(modifier = Modifier.height(14.dp))

            MyOutlinedTextField( //phone number text field
                text = loginViewModel.phoneNumberText.value,
                onTextChanged = loginViewModel::changePhoneNumberTextSetName,
                placeHolderText = "Phone Number",
                keyboardType = KeyboardType.Number,
                leadingIcon = Icons.Default.Star,
                leadingDescription = "Phone number icon",
                trailingDescription = "Clear icon",
                maxWidth
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (isCodeSent) {
                MyOutlinedTextField( //verification code text field
                    text = loginViewModel.phoneVerificationCodeText.value,
                    onTextChanged = loginViewModel::changePhoneVerificationCodeTextSetName,
                    placeHolderText = "Phone verification code",
                    keyboardType = KeyboardType.Number,
                    leadingIcon = Icons.Default.Star,
                    leadingDescription = "Verification code icon",
                    trailingDescription = "Clear icon",
                    maxWidth
                )
            }

            if (isCodeSent) {
                Spacer(modifier = Modifier.height(7.dp))
                Text("Check your phone")
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(onClick = {
                if (!isCodeSent) {
                    loginViewModel.startPhoneNumberVerification()
                } else {
                    loginViewModel.resendPhoneNumberVerificationCode()
                }
            }) {
                if (!isCodeSent) {
                    Text("Send")
                } else {
                    Text("Resend")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row {
                TextButton(onClick = {
                    onBackClick()
                    loginViewModel.changeIsPhoneCodeSent(false)
                }) { Text("Back") }

                if (isCodeSent) {
                    Spacer(modifier = Modifier.width(28.dp))
                    Button(onClick = {
                        loginViewModel.verifyPhoneNumberWithCode()
                    }) { Text("Next") }
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordContent(
    loginViewModel: LoginViewModel,
    onBackClick: () -> Unit,
    maxWidth: Dp,
) {
    val isEmailSent by remember { loginViewModel.isEmailForgotPasswordSent() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        MyOutlinedTextField(
            text = loginViewModel.signInMailText.value,
            onTextChanged = loginViewModel::signInMailTextSetName,
            placeHolderText = "Your email",
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
            leadingDescription = "Email icon",
            trailingDescription = "Clear icon",
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (isEmailSent) {
            Text("Check your email")
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = { loginViewModel.forgotPassword() }) {
            Text("Send")
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = {
            onBackClick()
            loginViewModel.changeIsEmailForgotPasswordSent(false)
        }) {
            Text("Back")
        }
    }
}

@Composable
private fun PasswordOutlinedTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    placeHolderText: String,
    keyboardType: KeyboardType,
    leadingIcon: ImageVector,
    leadingDescription: String,
    trailingDescription: String,
    maxWidth: Dp,
) {

    var passwordHidden by remember { mutableStateOf(true) }

    OutlinedTextField(
        value = text,
        onValueChange = onTextChanged,
        placeholder = { Text(text = placeHolderText) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .width((maxWidth / 100 * 70))
            .heightIn(max = 90.dp),
        maxLines = 1,
        singleLine = true,
        enabled = true,
        readOnly = false,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = leadingDescription
            )
        },
        visualTransformation =
        if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,

        trailingIcon = {
            IconButton(onClick =
            {
                passwordHidden =
                    !passwordHidden // loginViewModel.changePasswordHiddenState(!passwordHidden) /*{ changePasswordHiddenState(!passwordHidden) }*/
            }) {
                val visibilityIcon =
                    if (passwordHidden) Res.javaClass.classLoader?.getResourceAsStream("invisibility_eye_24.xml")
                    else Res.javaClass.classLoader?.getResourceAsStream("visibility_eye_24.xml")

                val bitmap = BitmapFactory.decodeStream(visibilityIcon)
                Icon(bitmap.asImageBitmap(), contentDescription = trailingDescription)
            }
        }
    )

}

@Composable
private fun MyOutlinedTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    placeHolderText: String,
    keyboardType: KeyboardType,
    leadingIcon: ImageVector,
    leadingDescription: String,
    trailingDescription: String,
    maxWidth: Dp,
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChanged,
        placeholder = { Text(text = placeHolderText) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .width((maxWidth / 100 * 70))
            .heightIn(max = 90.dp),
        maxLines = 1,
        singleLine = true,
        enabled = true,
        readOnly = false,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = leadingDescription
            )
        },
        trailingIcon = {
            IconButton(onClick = { onTextChanged("") }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = trailingDescription)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryCodePicker(
    loginViewModel: LoginViewModel,
    maxWidth: Dp,
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }

    var allCountryList by rememberSaveable(Unit) {
        mutableStateOf(listOf<Country?>(null))
    }

    val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        SheetState(
            false,
            LocalDensity.current
        )
    )

    LaunchedEffect(Unit) {
        allCountryList = loadAndParseJsonFile()!!.toList()
    }

    if (!openBottomSheet) {
        Box {
            CountryPickerTextField(
                label = "Select your country",
                modifier = Modifier
                    .width((maxWidth / 100 * 70))
                    .heightIn(max = 90.dp),
                selectedCountry = selectedCountry,
                defaultCountry = allCountryList.firstOrNull(),
                onShowCountryPicker = {
                    openBottomSheet = allCountryList.first() != null
                },
                isPickerVisible = openBottomSheet,
                countryList = allCountryList,
                iconDescription = "Arrow drop down icon"
            )
        }
    }

    LaunchedEffect(openBottomSheet) {
        if (!openBottomSheet) {
            scaffoldState.bottomSheetState.hide()
        }
    }

    if (openBottomSheet) {
        loginViewModel.changeColumnState(!openBottomSheet)

        CountryPickerBottomSheetScaffold(
            scaffoldState = scaffoldState,
            bottomSheetTitle = {},
            onItemSelected = {
                selectedCountry = it
                loginViewModel.changeColumnState(true)
                openBottomSheet = false
                loginViewModel.changePhoneAreaCodeTextSetName(it.areaCode)
            },
            countryList = allCountryList,
            searchTextFieldText = "Search your country",
            iconDescription = "Clear icon",
            titleText = "Select a country",
            onDismissRequest = {
                loginViewModel.changeColumnState(true)
                openBottomSheet = false
            },
            isBottomSheetVisible = openBottomSheet,
        )
    }
}