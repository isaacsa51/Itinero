/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: BalanceCircles.kt
 - Project: Itinero
 - Module: Itinero.designsystem-lib.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 17 julio 2025
 */

package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Utils.formatCurrency

/**
 * A reusable component that displays two overlapping circles representing financial balance.
 * The larger amount appears on top and the circles overlap for a modern design.
 *
 * @param youOwe The amount that you owe
 * @param youAreOwed The amount that you are owed
 * @param overlapOffset How much the circles overlap (default 68.dp)
 * @param minSize Minimum size for the circles (default 120.dp)
 * @param maxSize Maximum size for the circles (default 180.dp)
 * @param defaultSize Default size for the circles (default 140.dp)
 * @param oweColor Background color for the "You owe" circle
 * @param owedColor Background color for the "You are owed" circle
 * @param oweTextColor Text color for the "You owe" circle
 * @param owedTextColor Text color for the "You are owed" circle
 * @param modifier Modifier to be applied to the component
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BalanceCircles(
    youOwe: Double,
    youAreOwed: Double,
    overlapOffset: Dp = 60.dp,
    minSize: Dp = 120.dp,
    maxSize: Dp = 160.dp,
    defaultSize: Dp = 140.dp,
    oweColor: Color = MaterialTheme.colorScheme.tertiary,
    owedColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    oweTextColor: Color = MaterialTheme.colorScheme.surface,
    owedTextColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    modifier: Modifier = Modifier
) {
    val totalAmount = youOwe + youAreOwed
    val (oweSize, owedSize) = if (totalAmount > 0) {
        calculateCircleSizes(youOwe, youAreOwed, minSize, maxSize, defaultSize)
    } else {
        defaultSize to defaultSize
    }

    // Determine which circle should be on top based on amount
    val isOweOnTop = youOwe > youAreOwed

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Bottom circle (the one that appears behind)
            Box(
                modifier = Modifier
                    .size(if (isOweOnTop) owedSize else oweSize)
                    .offset(x = if (isOweOnTop) overlapOffset else -overlapOffset)
                    .clip(CircleShape)
                    .background(
                        if (isOweOnTop) owedColor else oweColor
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = if (isOweOnTop) "You are owed" else "You owe",
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            color = if (isOweOnTop) owedTextColor else oweTextColor
                        )
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        text = formatCurrency(if (isOweOnTop) youAreOwed.toString() else youOwe.toString()),
                        style = MaterialTheme.typography.titleMediumEmphasized.copy(
                            color = if (isOweOnTop) owedTextColor else oweTextColor
                        )
                    )
                }
            }

            // Top circle (the one that appears in front)
            Box(
                modifier = Modifier
                    .size(if (isOweOnTop) oweSize else owedSize)
                    .offset(x = if (isOweOnTop) -overlapOffset else overlapOffset)
                    .clip(CircleShape)
                    .background(
                        if (isOweOnTop) oweColor else owedColor
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = if (isOweOnTop) "You owe" else "You are owed",
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            color = if (isOweOnTop) oweTextColor else owedTextColor
                        )
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        text = formatCurrency(if (isOweOnTop) youOwe.toString() else youAreOwed.toString()),
                        style = MaterialTheme.typography.titleMediumEmphasized.copy(
                            color = if (isOweOnTop) oweTextColor else owedTextColor
                        )
                    )
                }
            }
        }
    }
}

/**
 * Calculates the sizes of the circles based on the amounts
 */
private fun calculateCircleSizes(
    youOwe: Double,
    youAreOwed: Double,
    minSize: Dp,
    maxSize: Dp,
    defaultSize: Dp
): Pair<Dp, Dp> {
    if (youOwe < 0.01 && youAreOwed < 0.01) {
        return defaultSize to defaultSize
    }

    val totalAmount = youOwe + youAreOwed
    val difference = kotlin.math.abs(youOwe - youAreOwed)

    if (difference / totalAmount < 0.1) {
        return defaultSize to defaultSize
    }

    return when {
        youOwe > youAreOwed -> {
            val ratio = (youOwe / youAreOwed).coerceAtMost(1.5)
            val oweSize =
                (defaultSize.value + (maxSize.value - defaultSize.value) * (ratio - 1) / 0.5).dp
            val owedSize =
                (defaultSize.value - (defaultSize.value - minSize.value) * (ratio - 1) / 0.5).dp
            oweSize to owedSize.coerceAtLeast(minSize)
        }

        youAreOwed > youOwe -> {
            val ratio = (youAreOwed / youOwe).coerceAtMost(1.5)
            val owedSize =
                (defaultSize.value + (maxSize.value - defaultSize.value) * (ratio - 1) / 0.5).dp
            val oweSize =
                (defaultSize.value - (defaultSize.value - minSize.value) * (ratio - 1) / 0.5).dp
            oweSize.coerceAtLeast(minSize) to owedSize
        }

        else -> defaultSize to defaultSize
    }
}

@ComponentPreview
@Composable
private fun BalanceCirclesPreview() {
    PreviewWrapper {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // Equal amounts
            Text(
                text = "Equal Amounts",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            BalanceCircles(
                youOwe = 100.0,
                youAreOwed = 100.0
            )

            // You owe more
            Text(
                text = "You Owe More",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            BalanceCircles(
                youOwe = 150.0,
                youAreOwed = 50.0
            )

            // You are owed more
            Text(
                text = "You Are Owed More",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            BalanceCircles(
                youOwe = 25.0,
                youAreOwed = 200.0
            )

            // Zero amounts
            Text(
                text = "Zero Amounts",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            BalanceCircles(
                youOwe = 0.0,
                youAreOwed = 0.0
            )
        }
    }
}