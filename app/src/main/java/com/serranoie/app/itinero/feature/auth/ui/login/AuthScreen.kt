package com.serranoie.app.itinero.feature.auth.ui.login

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystem.ui.theme.component.IButton
import com.serranoie.app.designsystem.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystem.ui.theme.component.IPasswordField
import com.serranoie.app.designsystem.ui.theme.component.ITextButton
import com.serranoie.app.designsystem.ui.theme.component.ITextField
import com.serranoie.app.itinero.R
import com.serranoie.app.itinero.navigation.Route

@Composable
fun AuthScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.padding(16.dp)) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f),
                painter = painterResource(R.drawable.image_login),
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
                leadingIcon = Icons.Rounded.Email
            )

            IPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password"
            )

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start) {

                Text(text = "Forgot password?", style= typography.labelLarge)
                ITextButton(
                    onClick = {
                        navController.navigate(Route.ForgotPassword.route)
                    },
                    enabled = true,
                    text = { Text("reset password") },
                    leadingIcon = null
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            IButton(
                onClick = {
                    navController.navigate(Route.HomeNavigation.route) {
                        popUpTo(Route.AuthNavigation.route) { inclusive = true }
                    }
                },
                text = { Text("Log in") },
                leadingIcon = null,
                modifier = Modifier
                    .fillMaxWidth()
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
                    modifier = Modifier.padding(start = 4.dp),
                    enabled = true,
                    text = { Text("Sign Up") },
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AuthScreenPreview() {
    PreviewWrapper {
        AuthScreen(navController = rememberNavController())
    }
}