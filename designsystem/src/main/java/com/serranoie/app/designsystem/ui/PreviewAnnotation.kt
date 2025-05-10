package com.serranoie.app.designsystem.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    device = "spec:parent=pixel,navigation=buttons", showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = true, apiLevel = 28
)
@Preview(
    device = "spec:width=1080px,height=2340px,dpi=440,cutout=punch_hole", showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true
)
annotation class ThemePreviews

@Preview(showBackground = true)
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
annotation class ComponentPreview