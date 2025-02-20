package org.serranoie.feature.itinero.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Facebook
import androidx.compose.material.icons.rounded.LogoDev
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.serranoie.app.itinero.ui.component.ItineroButton
import org.serranoie.app.itinero.ui.component.ItineroOutlineButton
import org.serranoie.app.itinero.ui.component.ItineroTextButton
import org.serranoie.core.itinero.designsystem.ui.component.PasswordInput
import org.serranoie.core.itinero.designsystem.ui.component.TextInput

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit, onRegisterSuccess: () -> Unit
) {

    BackHandler(enabled = true) {
        onNavigateBack()
    }

    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Sign up",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            text = "Create an account to get started using Itinero",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        // Information forms
        TextInput(
            label = { Text("Name(s)") },
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
        )
        Spacer(Modifier.height(4.dp))
        TextInput(
            label = { Text("Last name") },
            modifier = Modifier.fillMaxWidth(),
            value = lastName,
            onValueChange = { lastName = it },
        )
        Spacer(Modifier.height(4.dp))
        TextInput(
            label = { Text("Phone number") },
            modifier = Modifier.fillMaxWidth(),
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
        )
        Spacer(Modifier.height(4.dp))
        TextInput(
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
        )
        Spacer(Modifier.height(4.dp))
        PasswordInput(
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            isError = false,
            errorMessage = "Password is too short",
            leadingIcon = true
        )
        Spacer(Modifier.height(4.dp))
        PasswordInput(
            label = { Text("Confirm password") },
            modifier = Modifier.fillMaxWidth(),
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            isError = false,
            errorMessage = "Password don't match",
            leadingIcon = true
        )

        Spacer(Modifier.height(16.dp))

        ItineroButton(text = { Text("Register") },
            modifier = Modifier.fillMaxWidth(),
            onClick = { onRegisterSuccess() })

        Spacer(Modifier.height(16.dp))

        HorizontalDivider(thickness = 1.dp)

        Spacer(Modifier.height(16.dp))

        ItineroOutlineButton(text = { Text("Continue with Facebook") },
            modifier = Modifier.fillMaxWidth(),
            onClick = { },
            leadingIcon = { Icon(Icons.Rounded.Facebook, contentDescription = null) })

        Spacer(Modifier.height(8.dp))

        ItineroOutlineButton(text = { Text("Continue with Google") },
            modifier = Modifier.fillMaxWidth(),
            onClick = { },
            leadingIcon = { Icon(Icons.Rounded.LogoDev, contentDescription = null) })

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account?", style = MaterialTheme.typography.bodyMedium)
            ItineroTextButton(text = {
                Text(
                    "Log in here", style = MaterialTheme.typography.bodyMedium
                )
            }, onClick = { onNavigateBack() })
        }
    }
}