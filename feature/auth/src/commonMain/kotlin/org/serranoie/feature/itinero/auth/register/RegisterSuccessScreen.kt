package org.serranoie.feature.itinero.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterSuccessScreen(onNavigateToLogin: () -> Unit,
                          onNavigateBack: () -> Unit) {

    BackHandler(enabled = true) {
        onNavigateBack()
    }

    Column {
        Text("Registration successful!")
    }

}