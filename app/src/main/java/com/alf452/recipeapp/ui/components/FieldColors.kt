package com.alf452.recipeapp.ui.components

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Text field colors for use over the flat slate-gray menu background: white
 * text/label/placeholder/border regardless of the device's light/dark or
 * Material You dynamic color scheme, since that background isn't part of
 * the theme's own surface colors.
 */
@Composable
fun slateTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White,
    focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
    focusedBorderColor = Color.White.copy(alpha = 0.7f),
    unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
    cursorColor = Color.White
)
