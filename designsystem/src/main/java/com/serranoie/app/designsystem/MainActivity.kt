package com.serranoie.app.designsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.theme.ItineroTheme
import com.serranoie.app.designsystem.ui.theme.component.IPasswordField
import com.serranoie.app.designsystem.ui.theme.component.ITextField
import com.serranoie.app.designsystem.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystem.ui.theme.component.IButton
import com.serranoie.app.designsystem.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystem.ui.theme.component.ITextButton
import com.serranoie.app.designsystem.ui.theme.component.IBackground
import com.serranoie.app.designsystem.ui.theme.component.DateRangeToolbar
import com.serranoie.app.designsystem.ui.theme.component.card.ICard
import com.serranoie.app.designsystem.ui.theme.component.BottomSheetContent
import com.serranoie.app.designsystem.ui.theme.component.OtpInputField
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ItineroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DesignSystemShowcase(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DesignSystemShowcase(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Typography Section
        Text(
            "Typography",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Display Large",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            "Display Medium",
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            "Display Small",
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            "Headline Large",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            "Headline Medium",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            "Headline Small",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            "Title Large",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            "Title Medium",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Title Small",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            "Body Large",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            "Body Medium",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            "Body Small",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            "Label Large",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            "Label Medium",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            "Label Small",
            style = MaterialTheme.typography.labelSmall
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Colors Section
        Text(
            "Colors",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Primary: Primary, OnPrimary, PrimaryContainer, OnPrimaryContainer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            "Secondary: Secondary, OnSecondary, SecondaryContainer, OnSecondaryContainer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Text(
            "Tertiary: Tertiary, OnTertiary, TertiaryContainer, OnTertiaryContainer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
        
        Text(
            "Error: Error, OnError, ErrorContainer, OnErrorContainer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        // Buttons Section
        Text(
            "Buttons",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Primary Buttons
        Text("Primary Buttons", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        
        IButton(
            onClick = { },
            text = { Text("Primary Button") },
            importance = ButtonImportance.Primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        IButton(
            onClick = { },
            text = { Text("Secondary Button") },
            importance = ButtonImportance.Secondary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        IButton(
            onClick = { },
            text = { Text("Tertiary Button") },
            importance = ButtonImportance.Tertiary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        IButton(
            onClick = { },
            text = { Text("Error Button") },
            importance = ButtonImportance.Error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Outline Buttons
        Text("Outline Buttons", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        
        IOutlineButton(
            onClick = { },
            text = { Text("Primary Outline") },
            importance = ButtonImportance.Primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        IOutlineButton(
            onClick = { },
            text = { Text("Secondary Outline") },
            importance = ButtonImportance.Secondary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        IOutlineButton(
            onClick = { },
            text = { Text("Tertiary Outline") },
            importance = ButtonImportance.Tertiary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        IOutlineButton(
            onClick = { },
            text = { Text("Error Outline") },
            importance = ButtonImportance.Error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Text Button
        Text("Text Button", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        
        ITextButton(
            onClick = { },
            text = { Text("Text Button") }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Input Fields
        Text(
            "Input Fields",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        var textFieldValue by remember { mutableStateOf("") }
        var passwordFieldValue by remember { mutableStateOf("") }
        
        ITextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = "Username",
            placeholder = "Enter your username",
            leadingIcon = Icons.Default.Person
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ITextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = "Email",
            placeholder = "Enter your email",
            leadingIcon = Icons.Default.Email
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        IPasswordField(
            value = passwordFieldValue,
            onValueChange = { passwordFieldValue = it },
            label = "Password"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Background Component
        Text(
            "Background Component",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Since Background fills the entire screen, we'll just show a small example
        Box(modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()) {
            IBackground {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Content inside IBackground",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DateRangeToolbar
        Text(
            "DateRangeToolbar",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            DateRangeToolbar(date = LocalDate.now())

            Column(modifier = Modifier
                .weight(1f)
                .padding(16.dp)) {
                Text(
                    text = "Content for this date",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // OutlinedCard
        Text(
            "OutlinedCard Variants",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Basic card without header
        ICard(
            swipeable = false,
            isCompleted = false
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Basic Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "This is a simple card without header",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Card with colorful header
        ICard(
            swipeable = false,
            isCompleted = false,
            headerTitle = "Card with Header",
            headerColor = MaterialTheme.colorScheme.primaryContainer,
            headerTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "This card has a colorful header",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Swipeable card with header
        ICard(
            swipeable = true,
            isCompleted = false,
            onSwipe = { /* Handle swipe action */ },
            headerTitle = "Swipeable Card"
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Swipe me to mark as completed",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Sheet Content
        Text(
            "BottomSheetContent",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
        ) {
            BottomSheetContent(
                scannedCode = "https://example.com/qr-code-123",
                onCopy = { /* Copy action */ },
                onShare = { /* Share action */ },
                onClose = { /* Close action */ }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // OTP Input Field
        Text(
            "OTP Input Field",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        var otpValue by remember { mutableStateOf("123") }

        OtpInputField(
            otpText = otpValue,
            onOtpTextChange = { otp, _ -> otpValue = otp }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DesignSystemShowcasePreview() {
    ItineroTheme {
        DesignSystemShowcase()
    }
}