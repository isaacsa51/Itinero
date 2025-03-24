package com.serranoie.app.itinero.feature.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.theme.UiUtils.MediumPadding1
import com.serranoie.app.designsystem.ui.theme.component.IIconButton
import com.serranoie.app.designsystem.ui.theme.component.ITextButton
import kotlinx.coroutines.launch

@Composable
fun OnboardItem(
    modifier: Modifier = Modifier, page: Page, pagerState: PagerState
) {
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Image(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.60F),
                painter = painterResource(id = page.image),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                modifier = Modifier.padding(horizontal = 30.dp),
                text = page.title,
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                modifier = Modifier.padding(horizontal = 30.dp),
                text = page.description,
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (pagerState.currentPage > 0) {
                    ITextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        text = { Text("Back") },
                        leadingIcon = null,
                        modifier = Modifier.padding(end = MediumPadding1),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    )
                }
                if (pagerState.currentPage < pages.size || pages.size == 1) {
                    IIconButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        leadingIcon = Icons.Filled.ArrowForward,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = if (pagerState.currentPage == 0 && pages.size > 1) Modifier.fillMaxWidth() else Modifier
                    )
                }
            }
        }
    }
}
