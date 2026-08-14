package com.alf452.recipeapp.ui.components

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import com.alf452.recipeapp.ui.theme.LocalRecipeTextColor

/**
 * Text field colors for use over the flat slate-gray menu background: text,
 * label, placeholder, border, and cursor all follow the user's chosen menu
 * text color, since that background isn't part of the theme's own surface
 * colors.
 */
@Composable
fun slateTextFieldColors(): androidx.compose.material3.TextFieldColors {
    val textColor = LocalRecipeTextColor.current
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedLabelColor = textColor,
        unfocusedLabelColor = textColor,
        focusedPlaceholderColor = textColor.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = textColor.copy(alpha = 0.6f),
        focusedBorderColor = textColor.copy(alpha = 0.7f),
        unfocusedBorderColor = textColor.copy(alpha = 0.4f),
        cursorColor = textColor
    )
}
