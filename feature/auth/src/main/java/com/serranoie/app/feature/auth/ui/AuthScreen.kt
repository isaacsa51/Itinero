package com.serranoie.app.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.rounded.Email
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.DevicePreview
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystemlib.ui.theme.component.IPasswordField
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField
import com.serranoie.app.designsystemlib.ui.theme.component.ITextButton
import com.serranoie.app.designsystemlib.ui.theme.component.InputType
import com.serranoie.app.feature.auth.R
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(
    navController: NavHostController,
    uiState: StateFlow<AuthUiState>,
    onLogin: (String, String) -> Unit
) {
    val state by uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val hasError = state is AuthUiState.Error
    var showFieldErrors by remember { mutableStateOf(false) }
    var previousEmailValue by remember { mutableStateOf(email) }
    var previousPasswordValue by remember { mutableStateOf(password) }

    LaunchedEffect(state, email, password) {
        when (state) {
            is AuthUiState.Error -> {
                showFieldErrors = true
            }

            is AuthUiState.Loading -> {
                showFieldErrors = false
            }

            else -> {
                if (email != previousEmailValue || password != previousPasswordValue) {
                    showFieldErrors = false
                }
            }
        }
        previousEmailValue = email
        previousPasswordValue = password
    }

    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state) {
        if (state is AuthUiState.Success) {
            navController.navigate(Route.WelcomeNavigation.route) {
                popUpTo(Route.AuthNavigation.route) { inclusive = true }
            }
        }
    }

    Scaffold(modifier = Modifier.padding(16.dp)) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .testTag("AuthScreenColumn")
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .testTag("AuthImage"),
                painter = painterResource(R.drawable.auth_image),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Log in to your account",
                style = typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            ITextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Enter your email",
                leadingIcon = Icons.Rounded.Email,
                inputType = InputType.EMAIL,
                isError = showFieldErrors,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                ),
                modifier = Modifier.testTag("AuthEmailField")
            )

            IPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .testTag("AuthPasswordField"),
                isError = showFieldErrors,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (email.isNotBlank() && password.isNotBlank() && state !is AuthUiState.Loading) {
                            onLogin(email, password)
                        }
                    }
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Text(text = "Forgot password?", style = typography.labelLarge)
                ITextButton(
                    onClick = {
                        navController.navigate(Route.ForgotPassword.route)
                    },
                    enabled = true,
                    text = { Text("Reset password", style = typography.labelLarge) },
                    leadingIcon = null
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            IButton(
                onClick = {
                    onLogin(email, password)
                },
                text = {
                    if (state is AuthUiState.Loading) {
                        LoadingIndicator()
                    } else {
                        Text("Log in")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("AuthLoginButton"),
                enabled = state !is AuthUiState.Loading
            )

            if (state is AuthUiState.Error) {
                Text(
                    text = (state as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .testTag("AuthErrorText")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))


            IOutlineButton(
                onClick = { /* todo: implement button click handler */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("AuthFacebookButton"),
                enabled = true,
                text = { Text("Continue with Facebook") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Facebook, contentDescription = null
                    )
                },
                importance = ButtonImportance.Secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            IOutlineButton(
                onClick = { /* todo: implement button click handler */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("AuthGoogleButton"),
                enabled = true,
                text = { Text("Continue with Google") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.GppGood, contentDescription = null
                    )
                },
                importance = ButtonImportance.Secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Don't have an account?",
                    style = typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )

                ITextButton(
                    onClick = {
                        navController.navigate(Route.Register.route)
                    },
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .testTag("AuthSignUpButton"),
                    enabled = true,
                    text = { Text("Sign Up", style = typography.labelLarge) },
                )
            }
        }
    }
}

@DevicePreview
@Composable
private fun AuthScreenPreview() {
    PreviewWrapper {
        AuthScreen(
            navController = rememberNavController(),
            uiState = koinViewModel<AuthViewModel>().uiState,
            onLogin = { _, _ -> },
        )
    }
}
