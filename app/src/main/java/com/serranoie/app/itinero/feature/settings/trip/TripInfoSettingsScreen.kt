package com.serranoie.app.itinero.feature.settings.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews

@Composable
fun TripInfoSettingsScreen() {
    var name by remember { mutableStateOf("Hotel Hilton") }
    var checkIn by remember { mutableStateOf("10/02/2025 at 15:00 Hours") }
    var checkOut by remember { mutableStateOf("20/02/2025 at 12:00 Hours") }
    var phoneNumber by remember { mutableStateOf("20/02/2025 at 12:00 Hours") }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            Text("Accommodation name")
            CustomTextField(value = name, onValueChange = { name = it })

            Text("Check in date")
            CustomTextField(value = checkIn, onValueChange = { checkIn = it }, enabled = false)

            Text("Check out date")
            CustomTextField(value = checkOut, onValueChange = { checkOut = it }, enabled = false)

            Text("Accommodation phone number")
            CustomTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                enabled = false,
                leadingIcon = Icons.Default.Person
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Text("Save", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F8F5), RoundedCornerShape(12.dp)),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        },
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
//        colors = OutlinedTextFieldDefaults.colors(
//            containerColor = Color.Transparent,
//            unfocusedBorderColor = Color.Transparent,
//            focusedBorderColor = Color(0xFF008977), // ejemplo teal
//            textColor = Color.Black,
//            disabledTextColor = Color.Gray,
//            disabledLeadingIconColor = Color.Gray,
//            disabledPlaceholderColor = Color.Gray
//        ),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@ThemePreviews
@Composable
private fun TripInfoSettingsScreenPreview() {
    PreviewWrapper {
        TripInfoSettingsScreen()
    }
}