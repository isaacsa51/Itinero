package org.serranoie.core.itinero.navigation.auth

sealed class AuthFlowScreen {
    data object Login : AuthFlowScreen()
    data object Register : AuthFlowScreen()
    data object RegisterSuccess : AuthFlowScreen()
    data object ForgotPassword : AuthFlowScreen()
}