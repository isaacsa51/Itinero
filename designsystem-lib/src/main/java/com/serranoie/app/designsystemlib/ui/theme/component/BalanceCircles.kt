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
import com.serranoie.app.designsystemlib.ui.utils.Constants.BALANCE_CIRCLE_MAX_RATIO
import com.serranoie.app.designsystemlib.ui.utils.Constants.BALANCE_CIRCLE_MIN_AMOUNT
import com.serranoie.app.designsystemlib.ui.utils.Constants.BALANCE_CIRCLE_RATIO_SCALE
import com.serranoie.app.designsystemlib.ui.utils.Constants.BALANCE_CIRCLE_SIZE_RATIO_THRESHOLD
import com.serranoie.app.designsystemlib.ui.utils.Constants.balanceCircleDefaultSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.balanceCircleMaxSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.balanceCircleMinSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.balanceCircleOverlapOffset
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.mediumPadding
import com.serranoie.app.designsystemlib.ui.utils.Utils.formatCurrency
import com.serranoie.app.designsystemlib.ui.utils.standardPadding

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
    overlapOffset: Dp = balanceCircleOverlapOffset,
    minSize: Dp = balanceCircleMinSize,
    maxSize: Dp = balanceCircleMaxSize,
    defaultSize: Dp = balanceCircleDefaultSize,
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
            modifier = Modifier.padding(vertical = basePadding), contentAlignment = Alignment.Center
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
                    .standardPadding(), contentAlignment = Alignment.Center
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
                        text = formatCurrency(if (isOweOnTop) youAreOwed else youOwe),
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
                    .standardPadding(), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = if (isOweOnTop) "You owe" else "You are owed",
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            color = if (isOweOnTop) oweTextColor else owedTextColor
                        )
                    )
                    MarqueeText(
                        textAlign = TextAlign.Center,
                        text = formatCurrency(if (isOweOnTop) youOwe else youAreOwed),
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
    youOwe: Double, youAreOwed: Double, minSize: Dp, maxSize: Dp, defaultSize: Dp
): Pair<Dp, Dp> {
    if (youOwe < BALANCE_CIRCLE_MIN_AMOUNT && youAreOwed < BALANCE_CIRCLE_MIN_AMOUNT) {
        return defaultSize to defaultSize
    }

    val totalAmount = youOwe + youAreOwed
    val difference = kotlin.math.abs(youOwe - youAreOwed)

    if (difference / totalAmount < BALANCE_CIRCLE_SIZE_RATIO_THRESHOLD) {
        return defaultSize to defaultSize
    }

    return when {
        youOwe > youAreOwed -> {
            val ratio =
                if (youAreOwed > 0) (youOwe / youAreOwed).coerceAtMost(BALANCE_CIRCLE_MAX_RATIO) else BALANCE_CIRCLE_MAX_RATIO
            val oweSize =
                (defaultSize.value + (maxSize.value - defaultSize.value) * (ratio - 1) / BALANCE_CIRCLE_RATIO_SCALE).dp
            val owedSize =
                (defaultSize.value - (defaultSize.value - minSize.value) * (ratio - 1) / BALANCE_CIRCLE_RATIO_SCALE).dp
            oweSize to owedSize.coerceAtLeast(minSize)
        }

        youAreOwed > youOwe -> {
            val ratio =
                if (youOwe > 0) (youAreOwed / youOwe).coerceAtMost(BALANCE_CIRCLE_MAX_RATIO) else BALANCE_CIRCLE_MAX_RATIO
            val owedSize =
                (defaultSize.value + (maxSize.value - defaultSize.value) * (ratio - 1) / BALANCE_CIRCLE_RATIO_SCALE).dp
            val oweSize =
                (defaultSize.value - (defaultSize.value - minSize.value) * (ratio - 1) / BALANCE_CIRCLE_RATIO_SCALE).dp
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
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            modifier = Modifier.padding(basePadding)
        ) {
            // Equal amounts
            Text(
                text = "Equal Amounts",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            BalanceCircles(
                youOwe = 100.0, youAreOwed = 100.0
            )

            // You owe more
            Text(
                text = "You Owe More",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            BalanceCircles(
                youOwe = 150.0, youAreOwed = 50.0
            )

            // You are owed more
            Text(
                text = "You Are Owed More",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            BalanceCircles(
                youOwe = 25.0, youAreOwed = 200.0
            )

            // Zero amounts
            Text(
                text = "Zero Amounts",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            BalanceCircles(
                youOwe = 0.0, youAreOwed = 0.0
            )
        }
    }
}