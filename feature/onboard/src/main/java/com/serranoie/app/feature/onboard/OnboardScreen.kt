package com.serranoie.app.feature.onboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews

@Composable
fun OnboardScreen(
    onFinished: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                pageSize = PageSize.Fill,
            ) { pageIndex ->
                OnboardItem(
                    page = pages[pageIndex],
                    pagerState = pagerState,
                    onFinished = onFinished
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun OnboardScreenPreview() {
    PreviewWrapper {
        OnboardScreen(onFinished = { })
    }
}
