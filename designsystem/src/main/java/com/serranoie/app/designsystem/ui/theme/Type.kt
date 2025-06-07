package com.serranoie.app.designsystem.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.serranoie.app.designsystem.R

// Set of Material typography styles to start with
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    Font(R.font.archivo_variablefont)
)

val displayFontFamily = FontFamily(
    Font(R.font.chivo_variablefont)
)


@OptIn(ExperimentalTextApi::class)
val displayLargeFontFamily =
    FontFamily(
        Font(
            R.font.archivo_variablefont,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(900),
                FontVariation.width(300f),
            )
        )
    )

@OptIn(ExperimentalTextApi::class)
val displaySmallFontFamily =
    FontFamily(
        Font(
            R.font.archivo_variablefont,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(1000),
                FontVariation.width(150f),
            )
        )
    )

@OptIn(ExperimentalTextApi::class)
val headlineMediumFontFamily =
    FontFamily(
        Font(
            R.font.archivo_variablefont,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(1000),
                FontVariation.width(150f),
            )
        )
    )

@OptIn(ExperimentalTextApi::class)
val titleSmallFontFamily =
    FontFamily(
        Font(
            R.font.archivo_variablefont,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(1000),
                FontVariation.width(150f),
            )
        )
    )

@OptIn(ExperimentalTextApi::class)
val labelLargeFontFamily =
    FontFamily(
        Font(
            R.font.archivo_variablefont,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(500),
                FontVariation.width(200f),
            )
        )
    )

@OptIn(ExperimentalTextApi::class)
val labelSmallFontFamily =
    FontFamily(
        Font(
            R.font.archivo_variablefont,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(400),
                FontVariation.width(200f),
            )
        )
    )

@OptIn(ExperimentalTextApi::class)
val bodyLargeFontFamily =
    FontFamily(
        Font(
            R.font.archivo_variablefont,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(500),
                FontVariation.width(200f),
            )
        )
    )

// Default Material 3 typography values
val baseline = Typography()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val ItineroTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayLargeEmphasized = baseline.displayLargeEmphasized.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    displaySmallEmphasized = baseline.displaySmallEmphasized.copy(fontFamily = displaySmallFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineMediumEmphasized = baseline.headlineMediumEmphasized.copy(fontFamily = headlineMediumFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLargeEmphasized = baseline.titleLargeEmphasized.copy(fontFamily = displayLargeFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    titleSmallEmphasized = baseline.titleSmallEmphasized.copy(fontFamily = titleSmallFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyLargeEmphasized = baseline.bodyLargeEmphasized.copy(fontFamily = bodyLargeFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelLargeEmphasized = baseline.labelLargeEmphasized.copy(fontFamily = labelLargeFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
    labelSmallEmphasized = baseline.labelSmallEmphasized.copy(fontFamily = labelSmallFontFamily),
)