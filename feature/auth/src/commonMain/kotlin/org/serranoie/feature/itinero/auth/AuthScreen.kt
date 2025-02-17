package org.serranoie.feature.itinero.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.serranoie.core.itinero.designsystem.ui.component.PasswordInput
import org.serranoie.core.itinero.designsystem.ui.component.TextInput

@Composable
fun AuthScreen() {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Auth!")

            Spacer(Modifier.height(16.dp))

            TextInput(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = { username = it },
                leadingIcon = Icons.Outlined.Person,
            )

            Text(text = "Enter your password", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            PasswordInput(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                isError = false,
                errorMessage = "Password is too short"
            )
        }

    }
}