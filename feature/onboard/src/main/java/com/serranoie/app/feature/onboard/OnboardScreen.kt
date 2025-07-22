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
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.designsystemlib.ui.DevicePreview

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
                    modifier = Modifier.padding(bottom = 16.dp),
                    page = pages[pageIndex],
                    pagerState = pagerState,
                    onFinished = onFinished
                )
            }
        }
    }
}

@DevicePreview
@Composable
fun OnboardScreenPreview() {
    PreviewWrapper {
        OnboardScreen(onFinished = { })
    }
}
