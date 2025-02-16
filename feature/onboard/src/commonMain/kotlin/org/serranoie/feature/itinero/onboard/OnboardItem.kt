package org.serranoie.feature.itinero.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.serranoie.app.itinero.ui.component.ItineroTextButton

@Composable
fun OnboardItem(
    page: Page,
    onNext: () -> Unit,
    onBack: (() -> Unit)? = null,
    backLabel: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                painter = painterResource(page.image),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )

            Text(
                text = page.title,
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = page.description,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null && backLabel != null) {
                    ItineroTextButton(onClick = onBack) {
                        Text(text = backLabel)
                    }
                }

                CircleButton(
                    onClick = onNext,
                    icon = Icons.Filled.ArrowForward
                )
            }
        }
    }
}

@Composable
fun CircleButton(onClick: () -> Unit, icon: ImageVector) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Icon(
            imageVector = icon, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}