/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: InputFields.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 26 july 2025
 */

package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.DISABLED_ALPHA
import com.serranoie.app.designsystemlib.ui.utils.Constants.PLACEHOLDER_ALPHA
import com.serranoie.app.designsystemlib.ui.utils.Constants.PRIMARY_ALPHA
import com.serranoie.app.designsystemlib.ui.utils.Constants.SURFACE_DISABLED_ALPHA
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.textFieldHorizontalPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.textFieldIconSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.textFieldIconSpacing
import com.serranoie.app.designsystemlib.ui.utils.Constants.textFieldSmallHeight
import com.serranoie.app.designsystemlib.ui.utils.Constants.textFieldVerticalPadding
import kotlinx.coroutines.launch

enum class InputType {
    TEXT,
    NUMBER,
    EMAIL,
    PHONE,
    URI
}

private fun InputType.toKeyboardType(): KeyboardType = when (this) {
    InputType.TEXT -> KeyboardType.Text
    InputType.NUMBER -> KeyboardType.Number
    InputType.EMAIL -> KeyboardType.Email
    InputType.PHONE -> KeyboardType.Phone
    InputType.URI -> KeyboardType.Uri
}

@Composable
fun ITextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    inputType: InputType = InputType.TEXT,
    onDone: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: RoundedCornerShape = RoundedCornerShape(commonCornerRadius),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    fontStyle: TextStyle? = MaterialTheme.typography.bodyLarge,
    autoScrollToFocus: Boolean = true,
    isError: Boolean = false
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val finalKeyboardOptions = keyboardOptions.copy(
        keyboardType = inputType.toKeyboardType(),
        imeAction = if (onDone != null) ImeAction.Done else keyboardOptions.imeAction
    )

    val finalKeyboardActions = if (onDone != null) {
        KeyboardActions(onDone = { onDone() })
    } else keyboardActions

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        keyboardOptions = finalKeyboardOptions,
        keyboardActions = finalKeyboardActions,
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused && autoScrollToFocus) coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(
                alpha = PRIMARY_ALPHA
            ),
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else borderColor,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            errorLabelColor = MaterialTheme.colorScheme.error,
            unfocusedPrefixColor = MaterialTheme.colorScheme.outline,
            focusedPrefixColor = MaterialTheme.colorScheme.primary.copy(alpha = PRIMARY_ALPHA),
            unfocusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            focusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.outline,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        ),
        textStyle = fontStyle ?: MaterialTheme.typography.labelLarge,
        isError = isError
    )
}

@Composable
fun IFilledTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    inputType: InputType = InputType.TEXT,
    onDone: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: RoundedCornerShape = RoundedCornerShape(commonCornerRadius),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    autoScrollToFocus: Boolean = true,
    isError: Boolean = false
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val finalKeyboardOptions = keyboardOptions.copy(
        keyboardType = inputType.toKeyboardType(),
        imeAction = if (onDone != null) ImeAction.Done else keyboardOptions.imeAction
    )

    val finalKeyboardActions = if (onDone != null) {
        KeyboardActions(onDone = { onDone() })
    } else keyboardActions

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        keyboardOptions = finalKeyboardOptions,
        keyboardActions = finalKeyboardActions,
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused && autoScrollToFocus) coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
        shape = shape,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(
                alpha = PRIMARY_ALPHA
            ),
            unfocusedIndicatorColor = if (isError) MaterialTheme.colorScheme.error else borderColor,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            errorLabelColor = MaterialTheme.colorScheme.error,
            focusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            unfocusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.outline,
        ),
        isError = isError,
        trailingIcon = if (isError) {
            { Icon(imageVector = Icons.Filled.Error, contentDescription = "Error") }
        } else null
    )
}

@Composable
fun IPasswordField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onDone: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: RoundedCornerShape = RoundedCornerShape(commonCornerRadius),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    autoScrollToFocus: Boolean = true,
    isError: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val finalKeyboardOptions = keyboardOptions.copy(
        imeAction = if (onDone != null) ImeAction.Done else keyboardOptions.imeAction
    )

    val finalKeyboardActions = if (onDone != null) {
        KeyboardActions(onDone = { onDone() })
    } else keyboardActions

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
        keyboardOptions = finalKeyboardOptions,
        keyboardActions = finalKeyboardActions,
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused && autoScrollToFocus) coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(
                alpha = PRIMARY_ALPHA
            ),
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else borderColor,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            errorLabelColor = MaterialTheme.colorScheme.error,
            unfocusedPrefixColor = MaterialTheme.colorScheme.outline,
            focusedPrefixColor = MaterialTheme.colorScheme.primary.copy(alpha = PRIMARY_ALPHA),
            unfocusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            focusedLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.outline,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        ),
        isError = isError
    )
}


@Composable
fun ISmallerTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    inputType: InputType = InputType.TEXT,
    onDone: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: RoundedCornerShape = RoundedCornerShape(commonCornerRadius),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    fontSize: TextUnit = MaterialTheme.typography.bodySmall.fontSize,
    enabled: Boolean = true,
    autoScrollToFocus: Boolean = true,
    isError: Boolean = false
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val finalKeyboardOptions = keyboardOptions.copy(
        keyboardType = inputType.toKeyboardType(),
        imeAction = if (onDone != null) ImeAction.Done else keyboardOptions.imeAction
    )

    val finalKeyboardActions = if (onDone != null) {
        KeyboardActions(onDone = { onDone() })
    } else keyboardActions

    BasicTextField(
        value = value,
        onValueChange = if (enabled) onValueChange else { _ -> },
        singleLine = true,
        keyboardOptions = finalKeyboardOptions,
        keyboardActions = finalKeyboardActions,
        enabled = enabled,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                alpha = DISABLED_ALPHA
            ), fontSize = fontSize
        ),
        modifier = modifier
            .height(textFieldSmallHeight)
            .background(
                if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(
                    alpha = SURFACE_DISABLED_ALPHA
                ), shape
            )
            .border(
                1.dp,
                if (isError) MaterialTheme.colorScheme.error else if (enabled) borderColor else borderColor.copy(
                    alpha = SURFACE_DISABLED_ALPHA
                ),
                shape
            )
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused && autoScrollToFocus) coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(
                    horizontal = textFieldHorizontalPadding, vertical = textFieldVerticalPadding
                ), verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(textFieldIconSize),
                        tint = if (isError) MaterialTheme.colorScheme.error else if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = DISABLED_ALPHA
                        )
                    )
                    Spacer(modifier = Modifier.width(textFieldIconSpacing))
                }

                Box(Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder, style = LocalTextStyle.current.copy(
                                color = if (isError) MaterialTheme.colorScheme.error else if (enabled) MaterialTheme.colorScheme.outline.copy(alpha = PLACEHOLDER_ALPHA) else MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = DISABLED_ALPHA
                                ), fontSize = fontSize
                            )
                        )
                    }
                    innerTextField()
                }

                trailingIcon?.let {
                    Spacer(modifier = Modifier.width(textFieldIconSpacing))
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(textFieldIconSize),
                        tint = if (isError) MaterialTheme.colorScheme.error else if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = DISABLED_ALPHA
                        )
                    )
                }
            }
        }
    )
}

@Composable
fun IFilledSmallerTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    inputType: InputType = InputType.TEXT,
    onDone: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: RoundedCornerShape = RoundedCornerShape(commonCornerRadius),
    fontSize: TextUnit = MaterialTheme.typography.bodySmall.fontSize,
    enabled: Boolean = true,
    autoScrollToFocus: Boolean = true,
    isError: Boolean = false
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    val finalKeyboardOptions = keyboardOptions.copy(
        keyboardType = inputType.toKeyboardType(),
        imeAction = if (onDone != null) ImeAction.Done else keyboardOptions.imeAction
    )

    val finalKeyboardActions = if (onDone != null) {
        KeyboardActions(onDone = { onDone() })
    } else keyboardActions

    BasicTextField(
        value = value,
        onValueChange = if (enabled) onValueChange else { _ -> },
        singleLine = true,
        keyboardOptions = finalKeyboardOptions,
        keyboardActions = finalKeyboardActions,
        enabled = enabled,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                alpha = DISABLED_ALPHA
            ), fontSize = fontSize
        ),
        modifier = modifier
            .height(textFieldSmallHeight)
            .background(
                if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = SURFACE_DISABLED_ALPHA
                ), shape
            )
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused && autoScrollToFocus) coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(
                    horizontal = textFieldHorizontalPadding, vertical = textFieldVerticalPadding
                ), verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(textFieldIconSize),
                        tint = if (isError) MaterialTheme.colorScheme.error else if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = DISABLED_ALPHA
                        )
                    )
                    Spacer(modifier = Modifier.width(textFieldIconSpacing))
                }

                Box(Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder, style = LocalTextStyle.current.copy(
                                color = if (isError) MaterialTheme.colorScheme.error else if (enabled) MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = PLACEHOLDER_ALPHA
                                ) else MaterialTheme.colorScheme.onSurface.copy(alpha = DISABLED_ALPHA),
                                fontSize = fontSize
                            )
                        )
                    }
                    innerTextField()
                }

                trailingIcon?.let {
                    Spacer(modifier = Modifier.width(textFieldIconSpacing))
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(textFieldIconSize),
                        tint = if (isError) MaterialTheme.colorScheme.error else if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = DISABLED_ALPHA
                        )
                    )
                }
            }
        }
    )
}


@ComponentPreview
@Composable
private fun ITextFieldPreview() {
    PreviewWrapper {
        Column(
            modifier = Modifier.padding(vertical = smallPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ITextField(
                value = "Example Text",
                onValueChange = {},
                label = "Username",
                placeholder = "Enter your username",
                leadingIcon = Icons.Default.Person,
                isError = true
            )

            IFilledTextField(
                value = "Example Text",
                onValueChange = {},
                label = "Username",
                placeholder = "Enter your username",
                leadingIcon = Icons.Default.Person,
                isError = true
            )

            IPasswordField(
                value = "password123",
                onValueChange = {},
                label = "Password",
                isError = true
            )

            ISmallerTextField(
                value = "Sample text",
                onValueChange = {},
                placeholder = "Enter text",
                leadingIcon = Icons.Default.Person,
                isError = true
            )

            IFilledSmallerTextField(
                value = "Sample text",
                onValueChange = {},
                placeholder = "Enter text",
                leadingIcon = Icons.Default.Person,
                isError = true
            )
        }
    }
}
