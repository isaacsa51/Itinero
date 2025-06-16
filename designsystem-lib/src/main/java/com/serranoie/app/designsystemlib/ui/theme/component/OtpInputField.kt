package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OtpInputField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 5,
    enabled: Boolean = true,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
        }
    }

    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (enabled && it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text, it.text.length == otpCount)
            }
        },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                // Display fixed prefix "ITN-"
                val prefix = "ITN-"
                prefix.forEachIndexed { _, char ->
                    if (char == '-') {
                        Text(
                            modifier = Modifier.padding(horizontal = 0.dp, vertical = 2.dp),
                            text = char.toString(),
                            style = MaterialTheme.typography.headlineSmallEmphasized,
                            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .width(28.dp)
                                .border(
                                    1.dp,
                                    if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(4.dp),
                            text = char.toString(),
                            style = MaterialTheme.typography.headlineSmallEmphasized,
                            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        text = otpText,
                        enabled = enabled
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CharView(
    index: Int,
    text: String,
    enabled: Boolean
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> "0"
        index > text.length -> ""
        else -> text[index].toString()
    }
    Text(
        modifier = Modifier
            .width(28.dp)
            .border(
                1.dp, when {
                    isFocused && enabled -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline
                }, RoundedCornerShape(4.dp)
            )
            .padding(4.dp),
        text = char,
        style = MaterialTheme.typography.headlineSmallEmphasized,
        color = if (isFocused && enabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OtpDisplayField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        // Display fixed prefix "ITN-"
        val prefix = "ITN-"
        prefix.forEachIndexed { _, char ->
            if (char == '-') {
                Text(
                    modifier = Modifier.padding(horizontal = 0.dp, vertical = 2.dp),
                    text = char.toString(),
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    modifier = Modifier
                        .width(28.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp),
                    text = char.toString(),
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        // Display the OTP digits
        val displayText = otpText.take(otpCount).padEnd(otpCount, ' ')
        displayText.forEachIndexed { index, char ->
            Text(
                modifier = Modifier
                    .width(28.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp),
                text = if (char == ' ') "" else char.toString(),
                style = MaterialTheme.typography.headlineSmallEmphasized,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            if (index < otpCount - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@ComponentPreview
@Composable
private fun OtpInputFieldPreview() {
    PreviewWrapper {
        var otpValue by remember { mutableStateOf("") }
        OtpInputField(
            otpText = otpValue,
            onOtpTextChange = { otp, _ -> otpValue = otp }
        )
    }
}

@ComponentPreview
@Composable
private fun OtpInputFieldWithValuePreview() {
    PreviewWrapper {
        var otpValue by remember { mutableStateOf("72429") }
        OtpInputField(
            otpText = otpValue,
            onOtpTextChange = { otp, _ -> otpValue = otp }
        )
    }
}

@ComponentPreview
@Composable
private fun OtpInputFieldReadOnlyPreview() {
    PreviewWrapper {
        OtpInputField(
            otpText = "72429",
            enabled = false,
            onOtpTextChange = { _, _ -> }
        )
    }
}

@ComponentPreview
@Composable
private fun OtpDisplayFieldPreview() {
    PreviewWrapper {
        OtpDisplayField(otpText = "72429")
    }
}
