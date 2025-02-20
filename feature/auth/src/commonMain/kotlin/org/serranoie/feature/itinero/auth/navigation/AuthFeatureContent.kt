package org.serranoie.feature.itinero.auth.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.serranoie.core.itinero.navigation.auth.AuthFlowScreen
import org.serranoie.feature.itinero.auth.AuthScreen
import org.serranoie.feature.itinero.auth.forgotPassword.ForgotPasswordScreen
import org.serranoie.feature.itinero.auth.register.RegisterScreen
import org.serranoie.feature.itinero.auth.register.RegisterSuccessScreen

@Composable
fun AuthFeatureContent(
    //onFacebookLoginClick: () -> Unit,
    //onGoogleLoginClick: () -> Unit,
    onFinish: () -> Unit,
) {
    var currentAuthScreen by remember { mutableStateOf<AuthFlowScreen>(AuthFlowScreen.Login) }

    when (currentAuthScreen) {

        AuthFlowScreen.ForgotPassword -> ForgotPasswordScreen(
            onNavigateBack = {
                currentAuthScreen = AuthFlowScreen.Login
            },
            onFinish = { AuthFlowScreen.Login }
        )

        AuthFlowScreen.Login -> {
            AuthScreen(
                onNavigateToRegister = { currentAuthScreen = AuthFlowScreen.Register },
                onNavigateToForgotPassword = { currentAuthScreen = AuthFlowScreen.ForgotPassword },
                //onFacebookLoginClick = onFacebookLoginClick,
                //onGoogleLoginClick = onGoogleLoginClick,
                onFinish = onFinish
            )
        }

        AuthFlowScreen.Register -> RegisterScreen(
            onNavigateBack = { currentAuthScreen = AuthFlowScreen.Login },
            onRegisterSuccess = { currentAuthScreen = AuthFlowScreen.RegisterSuccess })

        AuthFlowScreen.RegisterSuccess -> RegisterSuccessScreen(
            onNavigateToLogin = { currentAuthScreen = AuthFlowScreen.Login },
            onNavigateBack = { currentAuthScreen = AuthFlowScreen.Login }
        )
    }


}