package org.serranoie.feature.itinero.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnboardItem(
    modifier: Modifier = Modifier,
    page: Page
) {
    Column(modifier = modifier.fillMaxSize()) {

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f),
            painter = painterResource(page.image),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )


        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 36.dp),
            text = page.title,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            modifier = Modifier.padding(horizontal = 36.dp),
            text = page.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}