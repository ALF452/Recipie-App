package com.alf452.recipeapp.ui.components

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Text field colors for use over the wooden cutting board / marble
 * backgrounds: black text/label/placeholder regardless of the device's
 * light/dark or Material You dynamic color scheme, since those backgrounds
 * aren't part of the theme's own surface colors.
 */
@Composable
fun darkTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedLabelColor = Color.Black,
    unfocusedLabelColor = Color.Black,
    focusedPlaceholderColor = Color.Black,
    unfocusedPlaceholderColor = Color.Black
)
