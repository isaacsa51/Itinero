package com.serranoie.app.designsystem.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.ThemePreviews
import com.serranoie.app.designsystemlib.ui.theme.component.ButtonImportance
import com.serranoie.app.designsystemlib.ui.theme.component.IButton
import com.serranoie.app.designsystemlib.ui.theme.component.IFilledSmallerTextField
import com.serranoie.app.designsystemlib.ui.theme.component.IIconButton
import com.serranoie.app.designsystemlib.ui.theme.component.IOutlineButton
import com.serranoie.app.designsystemlib.ui.theme.component.IPasswordField
import com.serranoie.app.designsystemlib.ui.theme.component.ISmallerTextField
import com.serranoie.app.designsystemlib.ui.theme.component.ITextButton
import com.serranoie.app.designsystemlib.ui.theme.component.ITextField

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 01 - Typography 
        AtomicSection(title = "01 Typography") {
            TypographyShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 02 - Color 
        AtomicSection(title = "02 Color ") {
            ColorShowcaseSection()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 03 - Button 
        AtomicSection(title = "03 Button ") {
            ButtonShowcase()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 04 - Input Field 
        AtomicSection(title = "04 Input Field ") {
            InputFieldShowcase()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AtomicSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TypographyShowcase() {
    val typographyStyles = listOf(
        "Display Large" to MaterialTheme.typography.displayLarge,
        "Display Large Emphasized" to MaterialTheme.typography.displayLargeEmphasized.copy(
            fontWeight = FontWeight.Black
        ),
        "Display Medium" to MaterialTheme.typography.displayMedium,
        "Display Medium Emphasized" to MaterialTheme.typography.displayMediumEmphasized,
        "Display Small" to MaterialTheme.typography.displaySmall,
        "Display Small Emphasized" to MaterialTheme.typography.displaySmallEmphasized,
        "Headline Large" to MaterialTheme.typography.headlineLarge,
        "Headline Large Emphasized" to MaterialTheme.typography.headlineLargeEmphasized,
        "Headline Medium" to MaterialTheme.typography.headlineMedium,
        "Headline Medium Emphasized" to MaterialTheme.typography.headlineMediumEmphasized,
        "Headline Small" to MaterialTheme.typography.headlineSmall,
        "Headline Small Emphasized" to MaterialTheme.typography.headlineSmallEmphasized,
        "Title Large" to MaterialTheme.typography.titleLarge,
        "Title Large Emphasized" to MaterialTheme.typography.titleLargeEmphasized,
        "Title Medium" to MaterialTheme.typography.titleMedium,
        "Title Medium Emphasized" to MaterialTheme.typography.titleMediumEmphasized,
        "Title Small" to MaterialTheme.typography.titleSmall,
        "Title Small Emphasized" to MaterialTheme.typography.titleSmallEmphasized,
        "Body Large" to MaterialTheme.typography.bodyLarge,
        "Body Large Emphasized" to MaterialTheme.typography.bodyLargeEmphasized,
        "Body Medium" to MaterialTheme.typography.bodyMedium,
        "Body Medium Emphasized" to MaterialTheme.typography.bodyMediumEmphasized,
        "Body Small" to MaterialTheme.typography.bodySmall,
        "Body Small Emphasized" to MaterialTheme.typography.bodySmallEmphasized,
        "Label Large" to MaterialTheme.typography.labelLarge,
        "Label Large Emphasized" to MaterialTheme.typography.labelLargeEmphasized,
        "Label Medium" to MaterialTheme.typography.labelMedium,
        "Label Medium Emphasized" to MaterialTheme.typography.labelMediumEmphasized,
        "Label Small" to MaterialTheme.typography.labelSmall,
        "Label Small Emphasized" to MaterialTheme.typography.labelSmallEmphasized,
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        typographyStyles.forEach { (name, style) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = style
                    )
                }
                Text(
                    text = "Aa",
                    style = style,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Text(text = "To insert a different font weight to an emphasized text, use function copy()", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ColorShowcaseSection() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Primary Colors
        ColorVariantGroup(
            colorName = "Primary",
            mainColor = MaterialTheme.colorScheme.primary,
            onMainColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            fixedColor = MaterialTheme.colorScheme.primaryFixed,
            onFixedColor = MaterialTheme.colorScheme.onPrimaryFixed,
            fixedDimColor = MaterialTheme.colorScheme.primaryFixedDim,
            onFixedVariantColor = MaterialTheme.colorScheme.onPrimaryFixedVariant
        )

        // Secondary Colors
        ColorVariantGroup(
            colorName = "Secondary",
            mainColor = MaterialTheme.colorScheme.secondary,
            onMainColor = MaterialTheme.colorScheme.onSecondary,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            fixedColor = MaterialTheme.colorScheme.secondaryFixed,
            onFixedColor = MaterialTheme.colorScheme.onSecondaryFixed,
            fixedDimColor = MaterialTheme.colorScheme.secondaryFixedDim,
            onFixedVariantColor = MaterialTheme.colorScheme.onSecondaryFixedVariant
        )

        // Tertiary Colors
        ColorVariantGroup(
            colorName = "Tertiary",
            mainColor = MaterialTheme.colorScheme.tertiary,
            onMainColor = MaterialTheme.colorScheme.onTertiary,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onTertiaryContainer,
            fixedColor = MaterialTheme.colorScheme.tertiaryFixed,
            onFixedColor = MaterialTheme.colorScheme.onTertiaryFixed,
            fixedDimColor = MaterialTheme.colorScheme.tertiaryFixedDim,
            onFixedVariantColor = MaterialTheme.colorScheme.onTertiaryFixedVariant
        )

        // Error Colors
        ColorVariantGroup(
            colorName = "Error",
            mainColor = MaterialTheme.colorScheme.error,
            onMainColor = MaterialTheme.colorScheme.onError,
            containerColor = MaterialTheme.colorScheme.errorContainer,
            onContainerColor = MaterialTheme.colorScheme.onErrorContainer,
            fixedColor = MaterialTheme.colorScheme.errorContainer, // Error doesn't have fixed variants
            onFixedColor = MaterialTheme.colorScheme.onErrorContainer,
            fixedDimColor = MaterialTheme.colorScheme.errorContainer,
            onFixedVariantColor = MaterialTheme.colorScheme.onErrorContainer
        )

        // Surface and Background Colors
        SurfaceAndBackgroundColors()
    }
}

@Composable
fun ColorVariantGroup(
    colorName: String,
    mainColor: Color,
    onMainColor: Color,
    containerColor: Color,
    onContainerColor: Color,
    fixedColor: Color,
    onFixedColor: Color,
    fixedDimColor: Color,
    onFixedVariantColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Main color box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    color = mainColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                text = colorName,
                color = onMainColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }

        // Variants row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Container
            ColorVariantBox(
                color = containerColor,
                onColor = onContainerColor,
                label = "Container",
                modifier = Modifier.weight(1f)
            )

            // On Container
            ColorVariantBox(
                color = onContainerColor,
                onColor = containerColor,
                label = "On Container",
                modifier = Modifier.weight(1f)
            )

            // Fixed (skip for Error)
            if (colorName != "Error") {
                ColorVariantBox(
                    color = fixedColor,
                    onColor = onFixedColor,
                    label = "Fixed",
                    modifier = Modifier.weight(1f)
                )

                // Fixed Dim
                ColorVariantBox(
                    color = fixedDimColor,
                    onColor = onFixedVariantColor,
                    label = "Fixed Dim",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ColorVariantBox(
    color: Color,
    onColor: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(6.dp)
            )
    ) {
        Text(
            text = label,
            color = onColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )
    }
}

@Composable
fun SurfaceAndBackgroundColors() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Surface & Background Colors",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Surface
            ColorVariantBox(
                color = MaterialTheme.colorScheme.surface,
                onColor = MaterialTheme.colorScheme.onSurface,
                label = "Surface",
                modifier = Modifier.weight(1f)
            )

            // Surface Container
            ColorVariantBox(
                color = MaterialTheme.colorScheme.surfaceContainer,
                onColor = MaterialTheme.colorScheme.onSurface,
                label = "Surface Container",
                modifier = Modifier.weight(1f)
            )

            // Surface Variant
            ColorVariantBox(
                color = MaterialTheme.colorScheme.surfaceVariant,
                onColor = MaterialTheme.colorScheme.onSurfaceVariant,
                label = "Surface Variant",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Background
            ColorVariantBox(
                color = MaterialTheme.colorScheme.background,
                onColor = MaterialTheme.colorScheme.onBackground,
                label = "Background",
                modifier = Modifier.weight(1f)
            )

            // Outline
            ColorVariantBox(
                color = MaterialTheme.colorScheme.outline,
                onColor = MaterialTheme.colorScheme.surface,
                label = "Outline",
                modifier = Modifier.weight(1f)
            )

            // Outline Variant
            ColorVariantBox(
                color = MaterialTheme.colorScheme.outlineVariant,
                onColor = MaterialTheme.colorScheme.onSurface,
                label = "Outline Variant",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ButtonShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Filled Buttons
        Text(
            text = "Filled Buttons",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IButton(
                onClick = { },
                text = { Text("Primary") },
                importance = ButtonImportance.Primary,
                modifier = Modifier.weight(1f)
            )
            IButton(
                onClick = { },
                text = { Text("Secondary") },
                importance = ButtonImportance.Secondary,
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IButton(
                onClick = { },
                text = { Text("Tertiary") },
                importance = ButtonImportance.Tertiary,
                modifier = Modifier.weight(1f)
            )
            IButton(
                onClick = { },
                text = { Text("Error") },
                importance = ButtonImportance.Error,
                modifier = Modifier.weight(1f)
            )
        }

        // Disabled State
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IButton(
                onClick = { },
                text = { Text("Disabled") },
                importance = ButtonImportance.Primary,
                enabled = false,
                modifier = Modifier.weight(1f)
            )
            IButton(
                onClick = { },
                text = { Text("With Icon") },
                importance = ButtonImportance.Primary,
                leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider()

        // Outline Buttons
        Text(
            text = "Outline Buttons",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IOutlineButton(
                onClick = { },
                text = { Text("Primary") },
                importance = ButtonImportance.Primary,
                modifier = Modifier.weight(1f)
            )
            IOutlineButton(
                onClick = { },
                text = { Text("Secondary") },
                importance = ButtonImportance.Secondary,
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IOutlineButton(
                onClick = { },
                text = { Text("Tertiary") },
                importance = ButtonImportance.Tertiary,
                modifier = Modifier.weight(1f)
            )
            IOutlineButton(
                onClick = { },
                text = { Text("Error") },
                importance = ButtonImportance.Error,
                modifier = Modifier.weight(1f)
            )
        }

        // Disabled and With Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IOutlineButton(
                onClick = { },
                text = { Text("Disabled") },
                importance = ButtonImportance.Primary,
                enabled = false,
                modifier = Modifier.weight(1f)
            )
            IOutlineButton(
                onClick = { },
                text = { Text("With Icon") },
                importance = ButtonImportance.Primary,
                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider()

        // Text Buttons
        Text(
            text = "Text Buttons",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ITextButton(
                onClick = { },
                text = { Text("Text Button") },
                modifier = Modifier.weight(1f)
            )
            ITextButton(
                onClick = { },
                text = { Text("Disabled") },
                enabled = false,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider()

        // Icon Buttons
        Text(
            text = "Icon Buttons",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IIconButton(
                onClick = { },
                leadingIcon = Icons.Default.Favorite,
                modifier = Modifier.weight(1f)
            )
            IIconButton(
                onClick = { },
                leadingIcon = Icons.Default.Settings,
                enabled = false,
                modifier = Modifier.weight(1f)
            )
        }

        // Button Sizes
        Text(
            text = "Button Sizes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            IButton(
                onClick = { },
                text = { Text("Small Button") },
                importance = ButtonImportance.Primary,
                height = 32.dp,
                modifier = Modifier.fillMaxWidth()
            )
            IButton(
                onClick = { },
                text = { Text("Medium Button (Default)") },
                importance = ButtonImportance.Primary,
                modifier = Modifier.fillMaxWidth()
            )
            IButton(
                onClick = { },
                text = { Text("Large Button") },
                importance = ButtonImportance.Primary,
                height = 56.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun InputFieldShowcase() {
    var textFieldValue by remember { mutableStateOf("") }
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var smallerTextValue by remember { mutableStateOf("") }
    var filledSmallerTextValue by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Standard Text Fields
        Text(
            text = "Standard Text Fields",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        ITextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = "Username",
            placeholder = "Enter your username",
            leadingIcon = Icons.Default.Person
        )

        ITextField(
            value = emailValue,
            onValueChange = { emailValue = it },
            label = "Email",
            placeholder = "Enter your email",
            leadingIcon = Icons.Default.Email
        )

        ITextField(
            value = "Disabled field",
            onValueChange = { },
            label = "Disabled",
            placeholder = "This field is disabled",
            leadingIcon = Icons.Default.Settings,
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        // Password Field
        Text(
            text = "Password Field",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        IPasswordField(
            value = passwordValue,
            onValueChange = { passwordValue = it },
            label = "Password"
        )

        HorizontalDivider()

        // Smaller Text Fields
        Text(
            text = "Compact Text Fields",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ISmallerTextField(
                value = smallerTextValue,
                onValueChange = { smallerTextValue = it },
                placeholder = "Smaller field",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.weight(1f)
            )
            
            IFilledSmallerTextField(
                value = filledSmallerTextValue,
                onValueChange = { filledSmallerTextValue = it },
                placeholder = "Filled smaller",
                leadingIcon = Icons.Default.Email,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ISmallerTextField(
                value = "Disabled",
                onValueChange = { },
                placeholder = "Disabled",
                leadingIcon = Icons.Default.Settings,
                enabled = false,
                modifier = Modifier.weight(1f)
            )
            
            IFilledSmallerTextField(
                value = "Disabled filled",
                onValueChange = { },
                placeholder = "Disabled filled",
                leadingIcon = Icons.Default.Phone,
                enabled = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@ThemePreviews
@Composable
fun HomeScreenPreview() {
    PreviewWrapper {
        HomeScreen()
    }
}