/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LabelChip.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 01 agosto 2025
 */

package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.standardPadding


/**
 * A simple chip component with a text
 *
 * @param modifier The modifier to be applied to the card
 * @param color The background color of the card (used with Surface for proper tonal elevation)
 * @param borderColor The color of the border around the card
 * @param text Text of the chip itself
 * @param textColor Text color of the chip
 * @param textStyle Text style of the chip
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LabelChip(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    textStyle: TextStyle = MaterialTheme.typography.labelMediumEmphasized,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant
) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = color,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = modifier.standardPadding(extraSmallPadding, extraSmallPadding)) {
            Text(
                text = text, style = textStyle, color = textColor
            )
        }
    }
}

@ComponentPreview
@Composable
private fun LabelChipPreview() {
    PreviewWrapper {
        Column(verticalArrangement = spacedBy(smallPadding)) {
            LabelChip(text = "OWNER")

            LabelChip(text = "Isaac Serrano")
        }
    }
}