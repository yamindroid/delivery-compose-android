package com.delivery.compose.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

val DarkColorScheme = darkColorScheme(
    primary = Orange40,
    onPrimary = WhiteColor,
    secondary = OrangeGrey40,
    tertiary = DeepOrange40,
    background = DarkBackgroundColor,
    surface = CardDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    surfaceVariant = SurfaceDark
)

val LightColorScheme = lightColorScheme(
    primary = Orange40,
    onPrimary = WhiteColor,
    secondary = OrangeGrey40,
    tertiary = DeepOrange40,
    background = LightBackgroundColor,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    surfaceVariant = SurfaceLight
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}