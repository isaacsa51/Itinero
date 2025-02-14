package org.serranoie.feature.itinero.onboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OnboardItem(
    modifier: Modifier = Modifier,
    page: Page
) {
    Column(modifier = modifier) {

        //Image

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

@Preview
@Composable
fun OnboardItemPreview() {
    OnboardItem(
        page = Page(
            title = "Welcome to Itinero",
            description = "Itinero is a travel app that helps you plan your next adventure."
        )
    )
}