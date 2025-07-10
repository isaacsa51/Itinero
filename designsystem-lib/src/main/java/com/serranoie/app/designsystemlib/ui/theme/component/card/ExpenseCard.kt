package com.serranoie.app.designsystemlib.ui.theme.component.card

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import java.util.Locale

/**
 * Same outline card but focused on displaying expense details.
 *
 * @param expenseName The name of the expense.
 * @param membersCount The number of members involved in the expense.
 * @param amountOwed The amount owed by the user.
 * @param modifier The modifier to apply to this layout node.
 * @param icon The icon to display on the card.
 * @param iconBackgroundColor The background color of the icon.
 * @param cardBackgroundColor The background color of the card.
 * @param borderColor The color of the border.
 * @param isCompleted Whether the card is in a completed state.
 */
@Composable
fun ExpenseCard(
    expenseName: String,
    membersCount: Int,
    amountOwed: Double,
    isCompleted: Boolean = false,
    isYours: Boolean = false,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.ConfirmationNumber,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.background,
    cardBackgroundColor: Color = if (isCompleted) MaterialTheme.colorScheme.secondaryContainer.copy(
        alpha = 0.75f
    ) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
    borderColor: Color = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.25f),
) {
    ICard(
        modifier = modifier,
        tonalElevation = 2.dp,
        color = CardDefaults.cardColors(containerColor = cardBackgroundColor).containerColor,
        borderColor = borderColor,
        content = {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExpenseInfo(
                    expenseName = expenseName,
                    membersCount = membersCount,
                    icon = icon,
                    iconBackgroundColor = iconBackgroundColor,
                    modifier = Modifier.weight(0.70f)
                )

                ExpenseAmount(
                    isCompleted = isCompleted,
                    isYours = isYours,
                    amountOwed = amountOwed,
                    modifier = Modifier.weight(0.30f)
                )
            }
        },
        onClick = { })
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpenseInfo(
    expenseName: String,
    membersCount: Int,
    icon: ImageVector,
    iconBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBackgroundColor)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.33f),
                    shape = RoundedCornerShape(8.dp)
                ), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Expense icon",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = expenseName, style = MaterialTheme.typography.titleMediumEmphasized.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ), maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$membersCount members", style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpenseAmount(
    isCompleted: Boolean, isYours: Boolean, amountOwed: Double, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.End
    ) {
        val secondaryTextColor = MaterialTheme.colorScheme.secondary.copy(0.75f)

        Text(
            text = when {
                isCompleted -> "Settled!"
                isYours -> "They owe you"
                else -> "You owe"
            }, style = MaterialTheme.typography.bodySmallEmphasized.copy(
                color = secondaryTextColor,
                fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isCompleted) {
                CompletedIndicator()
            } else {
                AmountIndicator(amountOwed)
            }
        }
    }
}

@Composable
private fun CompletedIndicator() {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = Icons.Rounded.Check,
            contentDescription = "Payment completed",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AmountIndicator(amountOwed: Double) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$${String.format(Locale.US, "%.2f", amountOwed)}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer
            )
        )
    }
}

@ComponentPreview
@Composable
private fun OutlinedCardPreview() {
    PreviewWrapper {
        Column(modifier = Modifier.padding(16.dp)) {
            // Regular expense card (current user owes money)
            ExpenseCard(
                expenseName = "Dinner at La Taquería",
                membersCount = 4,
                amountOwed = 56.75,
                icon = Icons.Default.Restaurant
            )

            Spacer(modifier = Modifier.padding(8.dp))

            // Expense card where others owe the user
            ExpenseCard(
                expenseName = "Movie Tickets",
                membersCount = 3,
                amountOwed = 32.50,
                isYours = true,
                icon = Icons.Default.ConfirmationNumber
            )

            Spacer(modifier = Modifier.padding(8.dp))

            // Completed expense card
            ExpenseCard(
                expenseName = "Groceries",
                membersCount = 2,
                amountOwed = 45.20,
                isCompleted = true,
                icon = Icons.Default.Restaurant
            )

            Spacer(modifier = Modifier.padding(8.dp))

            // Completed expense card that was yours
            ExpenseCard(
                expenseName = "Uber ride",
                membersCount = 3,
                amountOwed = 12.80,
                isYours = true,
                isCompleted = false,
                icon = Icons.Rounded.DirectionsCar
            )
        }
    }
}
