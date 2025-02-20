package org.serranoie.feature.itinero.auth.forgotPassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.serranoie.app.itinero.ui.component.ItineroButton
import org.serranoie.core.itinero.designsystem.ui.component.TextInput

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ForgotPasswordScreen(onNavigateBack: () -> Unit, onFinish: () -> Unit) {

    BackHandler(enabled = true) {
        onNavigateBack()
    }

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Password reset",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Insert below your email registered to send a recover code",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))


        TextInput(
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            leadingIcon = Icons.Outlined.Mail,
        )

        Spacer(Modifier.height(16.dp))

        ItineroButton(
            text = { Text("Log in") },
            modifier = Modifier.fillMaxWidth(),
            onClick = onFinish
        )
    }
}