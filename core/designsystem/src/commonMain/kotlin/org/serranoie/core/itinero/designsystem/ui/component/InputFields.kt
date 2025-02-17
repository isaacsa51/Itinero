package org.serranoie.core.itinero.designsystem.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private val AppTextInputColors: TextFieldColors
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        cursorColor = MaterialTheme.colorScheme.onBackground,
        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
        focusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
        focusedTrailingIconColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onBackground,
        errorBorderColor = MaterialTheme.colorScheme.onBackground,
        errorTextColor = MaterialTheme.colorScheme.onBackground,
        errorLeadingIconColor = MaterialTheme.colorScheme.onBackground,
        errorTrailingIconColor = MaterialTheme.colorScheme.onBackground,
        errorLabelColor = MaterialTheme.colorScheme.onBackground,
        errorSupportingTextColor = MaterialTheme.colorScheme.error,
        focusedSupportingTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedSupportingTextColor = MaterialTheme.colorScheme.onBackground
    )

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    onTrailingIconClick: () -> Unit = {},
    trailingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        modifier = modifier,
        colors = AppTextInputColors,
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            if (leadingIcon != null)
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = leadingIcon, contentDescription = null
                )
        },
        trailingIcon = {
            if (trailingIcon != null)
                IconButton(onClick = onTrailingIconClick) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = trailingIcon, contentDescription = null
                    )
                }
        },
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )

    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

@Composable
fun PasswordInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        colors = AppTextInputColors,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Password lock icon"
            )
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        }
    )

    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}