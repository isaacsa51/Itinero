package com.serranoie.app.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Facebook
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystemlib.ui.theme.component.IPasswordField
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import com.serranoie.app.designsystemlib.ui.theme.component.InputType
import com.serranoie.app.feature.auth.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel

data class FieldValidation(
    val isValid: Boolean, val errorMessage: String? = null
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    uiState: StateFlow<AuthUiState>,
    onRegister: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }

    var validationErrorMessage by remember { mutableStateOf<String?>(null) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }

    val state by uiState.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is AuthUiState.Success -> {
                navController.navigate(Route.WelcomeNavigation.route) {
                    popUpTo(Route.AuthNavigation.route) { inclusive = true }
                }
            }

            is AuthUiState.Error -> {
                authErrorMessage = (state as AuthUiState.Error).message
                validationErrorMessage = null // Clear validation error when auth error occurs
            }

            is AuthUiState.Loading -> {
                validationErrorMessage = null // Clear validation error when loading
                authErrorMessage = null // Clear auth error when new request starts
            }

            else -> {
                authErrorMessage = null
                validationErrorMessage = null
            }
        }
    }

    // Display either validation error or auth error
    val displayErrorMessage = validationErrorMessage ?: authErrorMessage

    Scaffold(modifier = Modifier.padding(16.dp)) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .testTag("RegisterScreenColumn")
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .testTag("RegisterImage"),
                painter = painterResource(R.drawable.register_image),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Sign up", style = typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Create an account to plan the perfect trip and discover new destinations",
                style = typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            FieldInformation(
                name = name,
                onNameChange = { name = it },
                lastName = lastName,
                onLastNameChange = { lastName = it },
                number = number,
                onNumberChange = { number = it },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                passwordConfirmation = passwordConfirmation,
                onPasswordConfirmationChange = { passwordConfirmation = it })

            if (displayErrorMessage != null) {
                Text(
                    text = displayErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = typography.labelSmall,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .testTag("RegisterErrorText")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            IButton(
                onClick = {
                validationErrorMessage = null

                val nameValidation = validateName(name)
                val lastNameValidation = validateName(lastName)
                val numberValidation = validatePhoneNumber(number)
                val emailValidation = validateEmail(email)
                val passwordValidation = validatePassword(password)
                val passwordConfirmationValidation =
                    validatePasswordConfirmation(password, passwordConfirmation)

                if (!nameValidation.isValid) {
                    validationErrorMessage = nameValidation.errorMessage
                    return@IButton
                }
                if (!lastNameValidation.isValid) {
                    validationErrorMessage = lastNameValidation.errorMessage
                    return@IButton
                }
                if (!numberValidation.isValid) {
                    validationErrorMessage = numberValidation.errorMessage
                    return@IButton
                }
                if (!emailValidation.isValid) {
                    validationErrorMessage = emailValidation.errorMessage
                    return@IButton
                }
                if (!passwordValidation.isValid) {
                    validationErrorMessage = passwordValidation.errorMessage
                    return@IButton
                }
                if (!passwordConfirmationValidation.isValid) {
                    validationErrorMessage = passwordConfirmationValidation.errorMessage
                    return@IButton
                }

                onRegister(name, lastName, number, email, password)
            },
                text = {
                    if (state is AuthUiState.Loading) {
                        LoadingIndicator()
                    } else {
                        Text("Register")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("RegisterButton"),
                enabled = state !is AuthUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            IOutlineButton(
                onClick = { /* Facebook logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("FacebookButton"),
                enabled = true,
                text = { Text("Continue with Facebook") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Facebook, contentDescription = null)
                },
                importance = ButtonImportance.Secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            IOutlineButton(
                onClick = { /* Google logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("GoogleButton"),
                enabled = true,
                text = { Text("Continue with Google") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.GppGood, contentDescription = null)
                },
                importance = ButtonImportance.Secondary
            )
        }
    }
}

@Composable
private fun FieldInformation(
    name: String,
    onNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    number: String,
    onNumberChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordConfirmation: String,
    onPasswordConfirmationChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    ITextField(
        value = name,
        onValueChange = onNameChange,
        label = "Name",
        placeholder = "Enter your name",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        inputType = InputType.TEXT,
        modifier = Modifier.testTag("NameField")
    )

    ITextField(
        value = lastName,
        onValueChange = onLastNameChange,
        label = "Last Name",
        placeholder = "Enter your last name",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        inputType = InputType.TEXT,
        modifier = Modifier.testTag("LastNameField")
    )

    ITextField(
        value = number,
        onValueChange = { input ->
            if (input.all { it.isDigit() }) {
                onNumberChange(input)
            }
        },
        label = "Number",
        placeholder = "Enter your number",
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        inputType = InputType.PHONE,
        modifier = Modifier.testTag("NumberField")
    )

    ITextField(
        value = email,
        onValueChange = onEmailChange,
        label = "Email",
        placeholder = "Enter your email",
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
        ),
        inputType = InputType.EMAIL,
        modifier = Modifier.testTag("EmailField")
    )

    IPasswordField(
        value = password,
        onValueChange = onPasswordChange,
        label = "Password",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier.testTag("PasswordField")
    )

    IPasswordField(
        value = passwordConfirmation,
        onValueChange = onPasswordConfirmationChange,
        label = "Confirm Password",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }),
        modifier = Modifier.testTag("ConfirmPasswordField")
    )
}

internal fun validateName(name: String): FieldValidation {
    return if (name.isBlank()) {
        FieldValidation(false, "Name is required")
    } else if (name.length < 2) {
        FieldValidation(false, "Name must be at least 2 characters")
    } else {
        FieldValidation(true)
    }
}

internal fun validatePhoneNumber(number: String): FieldValidation {
    val phoneRegex = "^[0-9]{10,15}$".toRegex()
    return if (number.isBlank()) {
        FieldValidation(false, "Phone number is required")
    } else if (!phoneRegex.matches(number)) {
        FieldValidation(false, "Phone number must be 10-15 digits")
    } else {
        FieldValidation(true)
    }
}

internal fun validateEmail(email: String): FieldValidation {
    val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
    return if (email.isBlank()) {
        FieldValidation(false, "Email is required")
    } else if (!emailRegex.matches(email)) {
        FieldValidation(false, "Please enter a valid email")
    } else {
        FieldValidation(true)
    }
}

internal fun validatePassword(password: String): FieldValidation {
    val passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$".toRegex()
    return if (password.isBlank()) {
        FieldValidation(false, "Password is required")
    } else if (password.length < 8) {
        FieldValidation(false, "Password must be at least 8 characters")
    } else if (!passwordRegex.matches(password)) {
        FieldValidation(
            false, "Password must contain uppercase, lowercase, number, and special character"
        )
    } else {
        FieldValidation(true)
    }
}

internal fun validatePasswordConfirmation(password: String, confirmation: String): FieldValidation {
    return if (confirmation.isBlank()) {
        FieldValidation(false, "Password confirmation is required")
    } else if (password != confirmation) {
        FieldValidation(false, "Passwords do not match")
    } else {
        FieldValidation(true)
    }
}

//@DevicePreview
//@Composable
//private fun RegisterScreenPreview() {
//    PreviewWrapper {
//        val navController = rememberNavController()
//        RegisterScreen(
//            navController = navController,
//            onRegister = { _, _, _, _, _ -> },
//        )
//    }
//}
