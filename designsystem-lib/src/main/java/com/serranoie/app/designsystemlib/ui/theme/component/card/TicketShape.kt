/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: TicketShape.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 07 julio 2025
 */

package com.serranoie.app.designsystemlib.ui.theme.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.mediumPadding
import kotlin.math.floor

class TicketShape(
    private val teethWidthDp: Float, private val teethHeightDp: Float
) : Shape {

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ) = Outline.Generic(Path().apply {

        moveTo(
            size.width * 0.99f, size.height * 0.01f
        )

        val teethHeightPx = teethHeightDp * density.density
        var fullTeethWidthPx = teethWidthDp * density.density
        var halfTeethWidthPx = fullTeethWidthPx / 2
        var currentDrawPositionX = size.width * 0.99f
        var teethBasePositionY = size.height * 0.01f + teethHeightPx
        val shapeWidthPx = size.width * 0.99f - size.width * 0.01f

        val teethCount = shapeWidthPx / fullTeethWidthPx
        val minTeethCount = floor(teethCount)

        if (teethCount != minTeethCount) {
            val newTeethWidthPx = shapeWidthPx / minTeethCount
            fullTeethWidthPx = newTeethWidthPx
            halfTeethWidthPx = fullTeethWidthPx / 2
        }

        var drawnTeethCount = 1

        lineTo(
            currentDrawPositionX - halfTeethWidthPx, teethBasePositionY + teethHeightPx
        )

        while (drawnTeethCount < minTeethCount) {

            currentDrawPositionX -= halfTeethWidthPx

            lineTo(
                currentDrawPositionX - halfTeethWidthPx, teethBasePositionY - teethHeightPx
            )

            currentDrawPositionX -= halfTeethWidthPx

            lineTo(
                currentDrawPositionX - halfTeethWidthPx, teethBasePositionY + teethHeightPx
            )

            drawnTeethCount++
        }

        currentDrawPositionX -= halfTeethWidthPx

        lineTo(
            currentDrawPositionX - halfTeethWidthPx, teethBasePositionY - teethHeightPx
        )

        // draw left edge
        lineTo(
            size.width * 0.01f, size.height * 0.99f
        )

        drawnTeethCount = 1
        teethBasePositionY = size.height * 0.99f - teethHeightPx
        currentDrawPositionX = size.width * 0.01f

        lineTo(
            currentDrawPositionX, teethBasePositionY + teethHeightPx
        )

        lineTo(
            currentDrawPositionX + halfTeethWidthPx, teethBasePositionY - teethHeightPx
        )

        while (drawnTeethCount < minTeethCount) {

            currentDrawPositionX += halfTeethWidthPx

            lineTo(
                currentDrawPositionX + halfTeethWidthPx, teethBasePositionY + teethHeightPx
            )

            currentDrawPositionX += halfTeethWidthPx

            lineTo(
                currentDrawPositionX + halfTeethWidthPx, teethBasePositionY - teethHeightPx
            )

            drawnTeethCount++
        }

        currentDrawPositionX += halfTeethWidthPx

        lineTo(
            currentDrawPositionX + halfTeethWidthPx, teethBasePositionY + teethHeightPx
        )

        close()
    })
}

@Composable
fun TicketView(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    teethWidthDp: Float = 15f,
    teethHeightDp: Float = 3f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                0.dp, shape = TicketShape(teethWidthDp, teethHeightDp), clip = true
            )
            .background(backgroundColor)
            .padding(mediumPadding)
    ) {
        content()
    }
}

@ComponentPreview
@Composable
private fun SimpleTicketPreview() {
    PreviewWrapper {
        TicketView(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            teethWidthDp = 12f,
            teethHeightDp = 6f
        ) {
            Column {
                Text("Flight AB123")
                Text("NYC → LAX")
            }
        }
    }
}