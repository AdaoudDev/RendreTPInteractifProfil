package com.example.tpahmaddaoudjetpackcompose.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Palette sombre
private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = Teal20,
    secondary = Indigo80,
    tertiary = Amber80
)

// Palette claire
private val LightColorScheme = lightColorScheme(
    primary = Teal40,
    secondary = Indigo40,
    tertiary = Amber40
)

@Composable
fun TPAhmadDAOUDJetpackComposeTheme(
    // clair ou sombre selon le réglage du téléphone
    darkTheme: Boolean = isSystemInDarkTheme(),
    // couleurs dynamiques désactivées pour garder ma palette
    dynamicColor: Boolean = false,
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
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}