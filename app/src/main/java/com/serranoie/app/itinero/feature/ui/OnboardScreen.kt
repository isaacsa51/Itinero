package com.serranoie.app.itinero.feature.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serranoie.app.designsystem.ui.PreviewWrapper
import com.serranoie.app.designsystem.ui.ThemePreviews
import com.serranoie.app.designsystem.ui.theme.ItineroTheme

@Composable
fun OnboardScreen() {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                pageSize = PageSize.Fill,
            ) { pageIndex ->
                OnboardItem(page = pages[pageIndex], pagerState = pagerState)
            }
        }
    }
}

@ThemePreviews
@Composable
fun OnboardScreenPreview() {
    PreviewWrapper {
        OnboardScreen()
    }
}
