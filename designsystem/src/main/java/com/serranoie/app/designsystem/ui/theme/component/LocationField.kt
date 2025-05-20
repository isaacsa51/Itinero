package com.serranoie.app.designsystem.ui.theme.component

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.PreviewWrapper
import androidx.core.net.toUri

/**
 * A custom field that displays location information and opens Google Maps when clicked.
 */
@Composable
fun LocationField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Default.LocationOn,
    trailingIcon: ImageVector = Icons.Default.Map,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    var isEditing by remember { mutableStateOf(false) }
    var editValue by remember { mutableStateOf(value) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        if (isEditing) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ITextField(
                    value = editValue,
                    onValueChange = { editValue = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = "",
                    placeholder = "Enter location address",
                    leadingIcon = leadingIcon
                )

                IconButton(
                    onClick = {
                        isEditing = false
                        onValueChange(editValue)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Save and View in Maps",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.clickable(
                    enabled = enabled && value.isNotEmpty(),
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    // Open Google Maps with the provided location
                    val gmmIntentUri = "geo:0,0?q=$value".toUri()
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                    
                    IconButton(onClick = { 
                        editValue = value
                        isEditing = true 
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LocationFieldPreview() {
    PreviewWrapper {
        Column(Modifier.padding(16.dp)) {
            LocationField(
                value = "123 Main St, New York, NY 10001",
                label = "Accommodation Location"
            )

            Spacer(modifier = Modifier.padding(8.dp))

            LocationField(
                value = "",
                label = "Empty Location (Disabled)"
            )
        }
    }
}