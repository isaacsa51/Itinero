package org.serranoie.core.itinero.designsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import org.serranoie.app.itinero.ui.theme.BackgroundDark
import org.serranoie.app.itinero.ui.theme.BackgroundLight
import org.serranoie.app.itinero.ui.theme.ErrorContainerDark
import org.serranoie.app.itinero.ui.theme.ErrorContainerLight
import org.serranoie.app.itinero.ui.theme.ErrorDark
import org.serranoie.app.itinero.ui.theme.ErrorLight
import org.serranoie.app.itinero.ui.theme.InverseOnSurfaceDark
import org.serranoie.app.itinero.ui.theme.InverseOnSurfaceLight
import org.serranoie.app.itinero.ui.theme.InversePrimaryDark
import org.serranoie.app.itinero.ui.theme.InversePrimaryLight
import org.serranoie.app.itinero.ui.theme.InverseSurfaceDark
import org.serranoie.app.itinero.ui.theme.InverseSurfaceLight
import org.serranoie.app.itinero.ui.theme.OnBackgroundDark
import org.serranoie.app.itinero.ui.theme.OnBackgroundLight
import org.serranoie.app.itinero.ui.theme.OnErrorContainerDark
import org.serranoie.app.itinero.ui.theme.OnErrorContainerLight
import org.serranoie.app.itinero.ui.theme.OnErrorDark
import org.serranoie.app.itinero.ui.theme.OnErrorLight
import org.serranoie.app.itinero.ui.theme.OnPrimaryContainerDark
import org.serranoie.app.itinero.ui.theme.OnPrimaryContainerLight
import org.serranoie.app.itinero.ui.theme.OnPrimaryDark
import org.serranoie.app.itinero.ui.theme.OnPrimaryLight
import org.serranoie.app.itinero.ui.theme.OnSecondaryContainerDark
import org.serranoie.app.itinero.ui.theme.OnSecondaryContainerLight
import org.serranoie.app.itinero.ui.theme.OnSecondaryDark
import org.serranoie.app.itinero.ui.theme.OnSecondaryLight
import org.serranoie.app.itinero.ui.theme.OnSurfaceDark
import org.serranoie.app.itinero.ui.theme.OnSurfaceLight
import org.serranoie.app.itinero.ui.theme.OnSurfaceVariantDark
import org.serranoie.app.itinero.ui.theme.OnSurfaceVariantLight
import org.serranoie.app.itinero.ui.theme.OnTertiaryContainerDark
import org.serranoie.app.itinero.ui.theme.OnTertiaryContainerLight
import org.serranoie.app.itinero.ui.theme.OnTertiaryDark
import org.serranoie.app.itinero.ui.theme.OnTertiaryLight
import org.serranoie.app.itinero.ui.theme.OutlineDark
import org.serranoie.app.itinero.ui.theme.OutlineLight
import org.serranoie.app.itinero.ui.theme.OutlineVariantDark
import org.serranoie.app.itinero.ui.theme.OutlineVariantLight
import org.serranoie.app.itinero.ui.theme.PrimaryContainerDark
import org.serranoie.app.itinero.ui.theme.PrimaryContainerLight
import org.serranoie.app.itinero.ui.theme.PrimaryDark
import org.serranoie.app.itinero.ui.theme.PrimaryLight
import org.serranoie.app.itinero.ui.theme.ScrimDark
import org.serranoie.app.itinero.ui.theme.ScrimLight
import org.serranoie.app.itinero.ui.theme.SecondaryContainerDark
import org.serranoie.app.itinero.ui.theme.SecondaryContainerLight
import org.serranoie.app.itinero.ui.theme.SecondaryDark
import org.serranoie.app.itinero.ui.theme.SecondaryLight
import org.serranoie.app.itinero.ui.theme.SurfaceBrightDark
import org.serranoie.app.itinero.ui.theme.SurfaceBrightLight
import org.serranoie.app.itinero.ui.theme.SurfaceContainerDark
import org.serranoie.app.itinero.ui.theme.SurfaceContainerHighDark
import org.serranoie.app.itinero.ui.theme.SurfaceContainerHighLight
import org.serranoie.app.itinero.ui.theme.SurfaceContainerHighestDark
import org.serranoie.app.itinero.ui.theme.SurfaceContainerHighestLight
import org.serranoie.app.itinero.ui.theme.SurfaceContainerLight
import org.serranoie.app.itinero.ui.theme.SurfaceContainerLowDark
import org.serranoie.app.itinero.ui.theme.SurfaceContainerLowLight
import org.serranoie.app.itinero.ui.theme.SurfaceContainerLowestDark
import org.serranoie.app.itinero.ui.theme.SurfaceContainerLowestLight
import org.serranoie.app.itinero.ui.theme.SurfaceDark
import org.serranoie.app.itinero.ui.theme.SurfaceDimDark
import org.serranoie.app.itinero.ui.theme.SurfaceDimLight
import org.serranoie.app.itinero.ui.theme.SurfaceLight
import org.serranoie.app.itinero.ui.theme.SurfaceVariantDark
import org.serranoie.app.itinero.ui.theme.SurfaceVariantLight
import org.serranoie.app.itinero.ui.theme.TertiaryContainerDark
import org.serranoie.app.itinero.ui.theme.TertiaryContainerLight
import org.serranoie.app.itinero.ui.theme.TertiaryDark
import org.serranoie.app.itinero.ui.theme.TertiaryLight

// Generated using MaterialKolor Builder version 1.0.1 (101)
// https://materialkolor.com/?color_seed=FF68A500&dark_mode=true&selected_preset_id=res-0

private val lightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight,
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
)

private val darkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
)

@Composable
fun ItineroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}