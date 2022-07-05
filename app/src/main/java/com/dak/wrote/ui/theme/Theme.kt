package com.dak.wrote.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.math.ln

typealias Material3 = MaterialTheme
typealias Material2 = androidx.compose.material.MaterialTheme

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
//    surfaceTintColor = md_theme_light_surfaceTintColor,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
//    surfaceTintColor = md_theme_dark_surfaceTintColor,
)

private val LocalColors = staticCompositionLocalOf { LightColors }
val MaterialTheme.customColors: ColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current

/**
 * Create a Material theme for the application
 */
@Composable
fun WroteTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (!useDarkTheme) dynamicLightColorScheme(LocalContext.current)
            else dynamicDarkColorScheme(LocalContext.current)
        }
        else -> {
            if (!useDarkTheme) {
                LightColors
            } else {
                DarkColors
            }
        }
    }


    val elevation = 3.dp
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    val color = colors.surfaceTint.copy(alpha = alpha).compositeOver(colors.surface)

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color)

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = {
            CompositionLocalProvider(LocalColors provides Material3.colorScheme) {
                androidx.compose.material.MaterialTheme(
                    colors = Colors(
                        primary = MaterialTheme.colorScheme.primary,
                        primaryVariant = MaterialTheme.colorScheme.primaryContainer,
                        secondary = MaterialTheme.colorScheme.secondary,
                        secondaryVariant = MaterialTheme.colorScheme.secondaryContainer,
                        background = MaterialTheme.colorScheme.background,
                        surface = MaterialTheme.colorScheme.surface,
                        error = MaterialTheme.colorScheme.error,
                        onPrimary = MaterialTheme.colorScheme.onPrimary,
                        onSecondary = MaterialTheme.colorScheme.onSecondary,
                        onBackground = MaterialTheme.colorScheme.onBackground,
                        onSurface = MaterialTheme.colorScheme.onSurface,
                        onError = MaterialTheme.colorScheme.onError,
                        isLight = !useDarkTheme
                    ),
                    content = content
                )
            }
        }
    )
}