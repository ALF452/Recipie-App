package com.alf452.recipeapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * The user's chosen text/icon color for the slate-gray menu screens (picked
 * via the palette button next to My Pantry / Import Recipe). Provided once
 * at the app root so every screen reads the same live value without each
 * one needing it threaded through as an explicit parameter.
 */
val LocalRecipeTextColor = compositionLocalOf { Color.White }

private val LightColors = lightColorScheme(
    primary = RecipeBrown,
    onPrimary = RecipeCream,
    secondary = RecipeGreen,
    background = RecipeCream,
    surface = RecipeCream
)

private val DarkColors = darkColorScheme(
    primary = RecipeBrownDark,
    onPrimary = RecipeCream,
    secondary = RecipeGreen
)

@Composable
fun RecipeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
