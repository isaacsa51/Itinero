package com.serranoie.app.designsystem.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    device = "spec:width=1080px,height=2340px,dpi=440,cutout=tall", showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true
)
@Preview(
    device = "spec:width=1080px,height=2340px,dpi=440,cutout=tall", showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true
)
annotation class ThemePreviews