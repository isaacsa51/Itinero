package com.serranoie.app.designsystemlib.ui.theme.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.utils.Constants.basePadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.borderStrokeWidth
import com.serranoie.app.designsystemlib.ui.utils.Constants.commonCornerRadius
import com.serranoie.app.designsystemlib.ui.utils.Constants.extraSmallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.iconSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.indicatorSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallIconSize
import com.serranoie.app.designsystemlib.ui.utils.Constants.smallPadding
import com.serranoie.app.designsystemlib.ui.utils.Constants.subtleElevation
import com.serranoie.app.designsystemlib.ui.utils.Utils.formatCurrency
import com.serranoie.app.designsystemlib.ui.utils.bounceClick
import com.serranoie.app.designsystemlib.ui.utils.designBorder
import com.serranoie.app.designsystemlib.ui.utils.standardPadding

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
    onClick: () -> Unit = {}
) {
    ICard(
        modifier = modifier.bounceClick { onClick() },
        color = CardDefaults.cardColors(containerColor = cardBackgroundColor).containerColor,
        borderColor = borderColor,
        content = {
            Row(
                modifier = Modifier
                    .standardPadding()
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
        onClick = onClick
    )
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
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(iconSize + basePadding)
                .clip(RoundedCornerShape(commonCornerRadius))
                .background(iconBackgroundColor)
                .designBorder(
                    width = borderStrokeWidth,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.33f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Expense icon",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(iconSize)
            )
        }

        Spacer(modifier = Modifier.width(smallPadding + extraSmallPadding))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = expenseName,
                style = MaterialTheme.typography.titleMediumEmphasized.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$membersCount members",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpenseAmount(
    isCompleted: Boolean,
    isYours: Boolean,
    amountOwed: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        val secondaryTextColor = MaterialTheme.colorScheme.secondary.copy(0.75f)

        Text(
            text = when {
                isCompleted -> "Settled!"
                isYours -> "They owe you"
                else -> "You owe"
            },
            style = MaterialTheme.typography.bodySmallEmphasized.copy(
                color = secondaryTextColor,
                fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.height(extraSmallPadding))

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
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(commonCornerRadius)
            )
            .designBorder(
                width = borderStrokeWidth,
                color = MaterialTheme.colorScheme.primary
            )
            .standardPadding(
                horizontal = smallPadding,
                vertical = extraSmallPadding
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(smallIconSize),
            imageVector = Icons.Rounded.Check,
            contentDescription = "Payment completed",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AmountIndicator(amountOwed: Double) {
    val formattedAmount = formatCurrency(amountOwed)

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(commonCornerRadius)
            )
            .designBorder(
                width = borderStrokeWidth,
                color = MaterialTheme.colorScheme.error
            )
            .standardPadding(
                horizontal = smallPadding,
                vertical = extraSmallPadding
            )
    ) {
        Text(
            text = formattedAmount,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@ComponentPreview
@Composable
private fun OutlinedCardPreview() {
    PreviewWrapper {
        Column(modifier = Modifier.standardPadding()) {
            ExpenseCard(
                expenseName = "Dinner at La Taquería",
                membersCount = 4,
                amountOwed = 56.75,
                icon = Icons.Default.Restaurant
            )

            Spacer(modifier = Modifier.height(smallPadding))

            ExpenseCard(
                expenseName = "Movie Tickets and extra information from this expense with a very long title that should be truncated",
                membersCount = 15,
                amountOwed = 999999.99,
                isYours = true,
                icon = Icons.Default.ConfirmationNumber
            )

            Spacer(modifier = Modifier.height(smallPadding))

            ExpenseCard(
                expenseName = "Groceries",
                membersCount = 2,
                amountOwed = 45.20,
                isCompleted = true,
                icon = Icons.Default.Restaurant
            )

            Spacer(modifier = Modifier.height(smallPadding))

            ExpenseCard(
                expenseName = "Uber ride with extra description that is way too long for the available space and should be truncated",
                membersCount = 3,
                amountOwed = 12.80,
                isYours = true,
                isCompleted = false,
                icon = Icons.Rounded.DirectionsCar
            )
        }
    }
}
