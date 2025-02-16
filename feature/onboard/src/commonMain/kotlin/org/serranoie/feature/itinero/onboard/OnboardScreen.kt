package org.serranoie.feature.itinero.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import itinero.feature.onboard.generated.resources.Res
import itinero.feature.onboard.generated.resources.onboard_welcome
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.serranoie.app.itinero.ui.component.ItineroButton
import org.serranoie.app.itinero.ui.component.ItineroTextButton
import org.serranoie.core.itinero.designsystem.ui.component.PagerIndicator


@Composable
fun OnboardScreen(
    onNavigate: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(initialPage = 0) {
            pages.size
        }

        val buttonState = remember {
            derivedStateOf {
                when (pagerState.currentPage) {
                    0 -> listOf("", "Next") //TODO: Create string values for each text
                    1 -> listOf("Back", "Next")
                    2 -> listOf("Back", "Get Started")
                    else -> listOf("", "") // ! This should never happen
                }
            }
        }

        TopSection(onNavigate = onNavigate)

        HorizontalPager(modifier = Modifier.weight(1f), state = pagerState) { index ->
            OnboardItem(page = pages[index])
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PagerIndicator(
                modifier = Modifier.width(52.dp),
                pagesSize = pages.size,
                selectedPage = pagerState.currentPage
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                val scope = rememberCoroutineScope()

                if (buttonState.value[0].isNotEmpty()) {
                    ItineroTextButton(text = { Text(text = buttonState.value[0]) }, onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    })
                }

                ItineroButton(modifier = Modifier.padding(start = 8.dp),
                    text = { Text(buttonState.value[1]) },
                    onClick = {
                        if (pagerState.currentPage == 2) {
                            onNavigate()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }

                    })
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
private fun TopSection(onNavigate: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(12.dp)
    ) {
        ItineroTextButton(
            onClick = { onNavigate() }, modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(text = "Skip", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun JourneyScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        JourneyContent()
    }

}


@Composable
fun JourneyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            .padding(16.dp), // Padding for inner content
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(0.6f)) {
                Image(painterResource(Res.drawable.onboard_welcome), contentDescription = "Circles")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to Itinero",
                style = MaterialTheme.typography.displayLarge ,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Our team of professionals will guide you through the process and find the best loan option for your unique situation. Get started on your path to homeownership today.\"",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.SpaceAround) {
                // Buttons
                CircleButton(onClick = { /*TODO*/ }, icon = Icons.Filled.ArrowBack)
                Spacer(modifier = Modifier.width(8.dp))
                CircleButton(onClick = { /*TODO*/ }, icon = Icons.Filled.ArrowForward)
            }

            Spacer(modifier = Modifier.height(32.dp))

            ItineroButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .width(250.dp) // Adjusted width for the button
                    .height(48.dp),
            ) {
                Text(
                    text = "Try for Free",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CircleButton(onClick: () -> Unit, icon: ImageVector) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Decorative element
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}