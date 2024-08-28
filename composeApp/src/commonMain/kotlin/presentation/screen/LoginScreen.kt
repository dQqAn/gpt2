import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import util.Localization


@Composable
fun BoxWithConstraintsScope.SignInContent(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel(),
    localization: Localization
) {
    val maxWidth = maxWidth

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        MyOutlinedTextField(
            text = loginViewModel.signInMailText.value,
            onTextChanged = loginViewModel::signInMailTextSetName,
            placeHolderText = localization!!.yourMail,
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
            leadingDescription = localization.mailIcon,
            trailingDescription = localization.clearIcon,
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        PasswordOutlinedTextField(
            text = loginViewModel.signInPasswordText.value,
            onTextChanged = loginViewModel::signInPasswordTextSetName,
            placeHolderText = localization.yourPassword,
            keyboardType = KeyboardType.Password,
            leadingIcon = Icons.Default.Lock,
            leadingDescription = localization.passwordIcon,
            trailingDescription = localization.visibilityIcon,
            maxWidth
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = {
            loginViewModel.signIn()
        }) { Text(localization.signIn) }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(onClick = {
            navController.navigate(route = Screen.ForgotPassword.route) {
                popUpTo(Screen.ForgotPassword.route) {
                    inclusive = true
                }
            }
        }) { Text(localization.forgotPassword) }

        Spacer(modifier = Modifier.height(14.dp))

        Button(onClick = {
            navController.navigate(route = Screen.SignUp.route) {
                popUpTo(Screen.SignUp.route) {
                    inclusive = true
                }
            }
        }) { Text(localization.createNewAccount) }

        Spacer(modifier = Modifier.height(28.dp))

        TextButton(modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                navController.navigate(route = Screen.Language.route) {
                    popUpTo(Screen.Language.route) {
                        inclusive = true
                    }
                }
            }) { Text(text = localization.selectYourLanguage) }
    }
}

@Composable
fun BoxWithConstraintsScope.SignUpContent(
    navController: NavController,
    localization: Localization,
    loginViewModel: LoginViewModel = viewModel(),
) {
    val maxWidth = maxWidth

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        MyOutlinedTextField(
            text = loginViewModel.signUpFirstMailText.value,
            onTextChanged = loginViewModel::signUpFirstMailTextSetName,
            placeHolderText = localization.yourMail,
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
            leadingDescription = localization.mailIcon,
            trailingDescription = localization.clearIcon,
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        MyOutlinedTextField(
            text = loginViewModel.signUpSecondMailText.value,
            onTextChanged = loginViewModel::signUpSecondMailTextSetName,
            placeHolderText = localization.confirmYourMail,
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
            leadingDescription = localization.mailIcon,
            trailingDescription = localization.clearIcon,
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        PasswordOutlinedTextField(
            text = loginViewModel.signUpFirstPasswordText.value,
            onTextChanged = loginViewModel::signUpFirstPasswordTextSetName,
            placeHolderText = localization.yourPassword,
            keyboardType = KeyboardType.Password,
            leadingIcon = Icons.Default.Lock,
            leadingDescription = localization.passwordIcon,
            trailingDescription = localization.visibilityIcon,
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        PasswordOutlinedTextField(
            text = loginViewModel.signUpSecondPasswordText.value,
            onTextChanged = loginViewModel::signUpSecondPasswordTextSetName,
            placeHolderText = localization.confirmYourPassword,
            keyboardType = KeyboardType.Password,
            leadingIcon = Icons.Default.Lock,
            leadingDescription = localization.passwordIcon,
            trailingDescription = localization.visibilityIcon,
            maxWidth
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = {
            loginViewModel.signUp()
        }) { Text(localization.next) }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(onClick = {
            navController.popBackStack()
        }) { Text(localization.youAlreadyHaveAnAccount) }

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
fun BoxWithConstraintsScope.MailVerificationContent(
    navController: NavController,
    localization: Localization,
    loginViewModel: LoginViewModel = viewModel(),
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
            Text(localization.checkYourMail)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = {
            loginViewModel.sendEmailVerification()
        }) {
            if (!isEmailSent) {
                Text(localization.send)
            } else {
                Text(localization.resend)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row {
            TextButton(onClick = {
                loginViewModel.signOut().apply {
                    loginViewModel.signOut()
                    navController.navigate(route = Screen.SignIn.route)
                    /*if (navController.previousBackStackEntry?.destination?.route == null) {
                        navController.navigate(route = Screen.SignIn.route)
                    } else {
                        navController.popBackStack()
                    }*/
                    loginViewModel.changeIsEmailVerificationSent(false)
                }
            }) { Text(localization.back) }

            Spacer(modifier = Modifier.width(28.dp))

            Button(onClick = {
                loginViewModel.reloadUser().apply {
                    if (loginViewModel.isEmailVerified() == true && !loginViewModel.email().isNullOrEmpty()) {
                        navController.navigate(route = Screen.PhoneVerification.route)
                    }
                }
            }) { Text(localization.next) }
        }
    }
}

@Composable
fun BoxWithConstraintsScope.PhoneVerificationContent(
    navController: NavController,
    localization: Localization,
    loginViewModel: LoginViewModel = viewModel(),
) {
    val maxWidth = maxWidth

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
                placeHolderText = localization.phoneNumber,
                keyboardType = KeyboardType.Number,
                leadingIcon = Icons.Default.Star,
                leadingDescription = localization.phoneNumberIcon,
                trailingDescription = localization.clearIcon,
                maxWidth
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (isCodeSent) {
                MyOutlinedTextField( //verification code text field
                    text = loginViewModel.phoneVerificationCodeText.value,
                    onTextChanged = loginViewModel::changePhoneVerificationCodeTextSetName,
                    placeHolderText = localization.phoneVerificationCode,
                    keyboardType = KeyboardType.Number,
                    leadingIcon = Icons.Default.Star,
                    leadingDescription = localization.verificationCodeIcon,
                    trailingDescription = localization.clearIcon,
                    maxWidth
                )
            }

            if (isCodeSent) {
                Spacer(modifier = Modifier.height(7.dp))
                Text(localization.checkYourPhone)
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
                    Text(localization.send)
                } else {
                    Text(localization.resend)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row {
                TextButton(onClick = {
                    loginViewModel.signOut()
                    navController.navigate(route = Screen.SignIn.route)
                    loginViewModel.changeIsPhoneCodeSent(false)
                }) { Text(localization.back) }

                if (isCodeSent) {
                    Spacer(modifier = Modifier.width(28.dp))
                    Button(onClick = {
                        loginViewModel.verifyPhoneNumberWithCode()
                    }) { Text(localization.next) }
                }
            }
        }
    }
}

@Composable
fun BoxWithConstraintsScope.ForgotPasswordContent(
    navController: NavController,
    localization: Localization,
    loginViewModel: LoginViewModel = viewModel(),
) {
    val maxWidth = maxWidth

    val isEmailSent by remember { loginViewModel.isEmailForgotPasswordSent() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        MyOutlinedTextField(
            text = loginViewModel.signInMailText.value,
            onTextChanged = loginViewModel::signInMailTextSetName,
            placeHolderText = localization.yourMail,
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email,
            leadingDescription = localization.mailIcon,
            trailingDescription = localization.clearIcon,
            maxWidth
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (isEmailSent) {
            Text(localization.checkYourMail)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = { loginViewModel.forgotPassword() }) {
            Text(localization.send)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = {
            navController.popBackStack()
            loginViewModel.changeIsEmailForgotPasswordSent(false)
        }) {
            Text(localization.back)
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
                passwordHidden = !passwordHidden
                // loginViewModel.changePasswordHiddenState(!passwordHidden) /*{ changePasswordHiddenState(!passwordHidden) }*/
            }) {
                val visibilityIcon =
//                    if (passwordHidden) Res.javaClass.classLoader.getResourceAsStream("invisibility_eye_24.xml")
//                    else Res.javaClass.classLoader.getResourceAsStream("visibility_eye_24.xml")

                    if (passwordHidden) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown

//                val bitmap = BitmapFactory.decodeStream(visibilityIcon)
//                Icon(bitmap.asImageBitmap(), contentDescription = trailingDescription)
                Icon(imageVector = visibilityIcon, contentDescription = trailingDescription)
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
        loginViewModel.changeColumnState(true)

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
