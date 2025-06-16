package com.serranoie.app.designsystemlib.ui.theme.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.ComponentPreview
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A component for displaying a date in a vertical format, typically used in date range views.
 *
 * @param modifier The modifier to be applied to the component
 * @param date The date to display
 * @param dayFormatter The formatter for the day part of the date
 * @param monthFormatter The formatter for the month part of the date
 * @param dayTextStyle The text style for the day text
 * @param monthTextStyle The text style for the month text
 * @param dayColor The color for the day text
 * @param monthColor The color for the month text
 * @param horizontalAlignment The horizontal alignment for the text
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RowScope.DateRangeToolbar(
    modifier: Modifier = Modifier,
    date: LocalDate,
    dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd"),
    monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM"),
    dayTextStyle: TextStyle = MaterialTheme.typography.bodyLargeEmphasized,
    monthTextStyle: TextStyle = MaterialTheme.typography.labelSmallEmphasized,
    dayColor: Color = MaterialTheme.colorScheme.onSurface,
    monthColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    weight: Float = 0.15f
) {
    Column(
        modifier = modifier.weight(weight),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = date.format(dayFormatter),
            style = dayTextStyle,
            color = dayColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = date.format(monthFormatter).uppercase(),
            style = monthTextStyle,
            color = monthColor
        )
    }
}

@ComponentPreview
@Composable
private fun DateRangeToolbarPreview() {
    val today = LocalDate.now()
    
    PreviewWrapper {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp)
        ) {
            DateRangeToolbar(date = today)
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Content for this date",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
