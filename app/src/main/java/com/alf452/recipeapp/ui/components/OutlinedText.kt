package com.alf452.recipeapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

/**
 * White text with a black outline, for legibility over the busy wooden
 * cutting board background — plain solid text of either color tends to get
 * lost in the wood grain at a glance. Draws the same text twice: once
 * stroked in black behind, once filled in white on top, so it pops against
 * any part of the board regardless of how light or dark that patch is.
 */
@Composable
fun OutlinedText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Box(modifier = modifier) {
        Text(
            text = text,
            style = style.copy(
                color = Color.Black,
                drawStyle = Stroke(width = 4f, join = StrokeJoin.Round)
            ),
            maxLines = maxLines,
            overflow = overflow
        )
        Text(
            text = text,
            style = style.copy(color = Color.White),
            maxLines = maxLines,
            overflow = overflow
        )
    }
}
