package com.serranoie.app.designsystemlib.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_NIGHT_YES,
    device = "spec:parent=pixel_5,navigation=buttons",
)
annotation class DevicePreview

@Preview(
    name = "Light Mode",
    showBackground = true,
    device = "spec:parent=pixel_5,navigation=buttons",
    showSystemUi = false
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
annotation class ComponentPreview
