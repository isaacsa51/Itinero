package org.serranoie.feature.itinero.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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