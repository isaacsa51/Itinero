package org.serranoie.feature.itinero.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import itinero.feature.auth.generated.resources.Res
import itinero.feature.auth.generated.resources.auth_label_image
import org.jetbrains.compose.resources.painterResource
import org.serranoie.app.itinero.ui.component.ItineroButton
import org.serranoie.app.itinero.ui.component.ItineroOutlineButton
import org.serranoie.app.itinero.ui.component.ItineroTextButton
import org.serranoie.core.itinero.designsystem.ui.component.PasswordInput
import org.serranoie.core.itinero.designsystem.ui.component.TextInput

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(
    onNavigateToRegister: () -> Unit, onNavigateToForgotPassword: () -> Unit,
    //onFacebookLoginClick: () -> Unit,
    //onGoogleLoginClick: () -> Unit,
    onFinish: () -> Unit
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.height(8.dp))

        Image(
            painter = painterResource(Res.drawable.auth_label_image),
            contentDescription = "App Logo",
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Log in to your account",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(8.dp))

        TextInput(
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = { username = it },
            leadingIcon = Icons.Outlined.Person,
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

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Forgot your password? ", style = MaterialTheme.typography.bodyMedium)
            ItineroTextButton(text = {
                Text(
                    "Restore here", style = MaterialTheme.typography.bodyMedium
                )
            }, onClick = onNavigateToForgotPassword)
        }

        Spacer(Modifier.height(16.dp))

        ItineroButton(
            text = { Text("Log in") }, modifier = Modifier.fillMaxWidth(), onClick = onFinish
        )

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
            Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium)
            ItineroTextButton(text = {
                Text(
                    "Sign up", style = MaterialTheme.typography.bodyMedium
                )
            }, onClick = onNavigateToRegister)
        }
    }
}