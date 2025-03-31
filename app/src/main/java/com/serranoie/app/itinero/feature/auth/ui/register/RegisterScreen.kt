package com.serranoie.app.itinero.feature.auth.ui.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Facebook
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystem.ui.theme.component.IButton
import com.serranoie.app.designsystem.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystem.ui.theme.component.IPasswordField
import com.serranoie.app.designsystem.ui.theme.component.ITextField
import com.serranoie.app.itinero.navigation.Route

@Composable
fun RegisterScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }


    Scaffold(modifier = Modifier.padding(16.dp)) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Sign up", style = typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Create an account to plan the perfect trip and discover new destinations",
                style = typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            ITextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                placeholder = "Enter your name",
                keyboardOptions = KeyboardOptions.Default,
            )

            ITextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                placeholder = "Enter your last name",
            )

            ITextField(
                value = number,
                onValueChange = { number = it },
                label = "Number",
                placeholder = "Enter your number",
            )

            ITextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Enter your email",
            )

            IPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
            )

            IPasswordField(
                value = passwordConfirmation,
                onValueChange = { passwordConfirmation = it },
                label = "Confirm Password",
            )

            Spacer(modifier = Modifier.height(16.dp))

            IButton(
                onClick = {
                    navController.navigate(Route.AuthNavigation.route)
                },
                text = { Text("Register") },
                leadingIcon = null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            IOutlineButton(
                onClick = { /* todo: implement button click handler */ },
                modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                text = { Text("Continue with Google") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.GppGood, contentDescription = null
                    )
                },
                importance = ButtonImportance.Secondary
            )
        }
    }
}

@ThemePreviews
@Composable
private fun RegisterScreenPreview() {
    PreviewWrapper {
        RegisterScreen(navController = rememberNavController())
    }
}