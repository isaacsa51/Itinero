package com.serranoie.app.designsystem.ui.theme.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.PreviewWrapper

@Composable
fun ITextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            unfocusedBorderColor = borderColor,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            unfocusedPrefixColor = MaterialTheme.colorScheme.outline,
            focusedPrefixColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.outline,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@Composable
fun IPasswordField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    shape: RoundedCornerShape = RoundedCornerShape(size = 8.dp),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text("Enter your password") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Lock, contentDescription = "Lock Icon"
            )
        },
        trailingIcon = {
            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = icon, contentDescription = "Toggle Password Visibility")
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            unfocusedBorderColor = borderColor,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            unfocusedPrefixColor = MaterialTheme.colorScheme.outline,
            focusedPrefixColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.outline,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@Composable
@Preview
fun ITextFieldPreview() {
    PreviewWrapper {
        ITextField(
            value = "Example Text",
            onValueChange = {},
            label = "Username",
            placeholder = "Enter your username",
            leadingIcon = Icons.Default.Person,
        )
    }
}

@Composable
@Preview
fun IPasswordFieldPreview() {
    PreviewWrapper {
        IPasswordField(
            value = "password123",
            onValueChange = {},
            label = "Password",
        )
    }
}