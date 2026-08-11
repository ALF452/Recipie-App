package com.alf452.recipeapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

private val MarbleLight = Color(0xFFFAF8F4)
private val MarbleShade = Color(0xFFEDEAE3)
private val MottleColor = Color(0xFFDAD6CC)
private val VeinDark = Color(0xFF9B968C)
private val VeinMid = Color(0xFFB8B3A8)
private val VeinLight = Color(0xFFFFFFFF)
private val CounterShadow = Color(0xFF6B5B4A)

/**
 * A white marble countertop, replacing the flat/illustrated banner behind the
 * main menu's top app bar — paired with the wooden cutting board background
 * that fills the rest of the screen, so the whole main menu reads as a board
 * resting on a counter. Fully procedural (Canvas), no image assets.
 */
@Composable
fun MarbleCountertopBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawMarble()
    }
}

private fun DrawScope.drawMarble() {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return

    drawRect(
        brush = Brush.verticalGradient(colors = listOf(MarbleLight, MarbleShade), startY = 0f, endY = h),
        size = Size(w, h)
    )

    drawMottling(w, h)
    drawVein(
        w, h,
        points = listOf(0.06f to 0.10f, 0.22f to 0.30f, 0.18f to 0.52f, 0.38f to 0.68f, 0.34f to 0.92f),
        color = VeinDark, strokeWidth = 2.2f, alpha = 0.55f
    )
    drawVein(
        w, h,
        points = listOf(0.22f to 0.30f, 0.44f to 0.20f, 0.62f to 0.34f, 0.58f to 0.10f),
        color = VeinMid, strokeWidth = 1.4f, alpha = 0.4f
    )
    drawVein(
        w, h,
        points = listOf(0.55f to 0.85f, 0.68f to 0.60f, 0.66f to 0.35f, 0.80f to 0.22f, 0.90f to 0.30f),
        color = VeinDark, strokeWidth = 2f, alpha = 0.5f
    )
    drawVein(
        w, h,
        points = listOf(0.66f to 0.35f, 0.78f to 0.48f, 0.92f to 0.55f),
        color = VeinMid, strokeWidth = 1.2f, alpha = 0.35f
    )
    drawVein(
        w, h,
        points = listOf(0.02f to 0.75f, 0.14f to 0.85f, 0.10f to 0.98f),
        color = VeinMid, strokeWidth = 1.1f, alpha = 0.3f
    )

    // faint polish highlight running near the main veins
    drawVein(
        w, h,
        points = listOf(0.08f to 0.12f, 0.24f to 0.32f, 0.20f to 0.54f),
        color = VeinLight, strokeWidth = 1.5f, alpha = 0.25f
    )

    // soft shadow along the bottom edge, where the cutting board begins,
    // to read as the board resting on top of the counter
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, CounterShadow.copy(alpha = 0.22f)),
            startY = h * 0.82f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.82f),
        size = Size(w, h * 0.18f)
    )
}

private fun DrawScope.drawMottling(w: Float, h: Float) {
    val spots = listOf(
        Triple(0.14f, 0.22f, 0.10f),
        Triple(0.55f, 0.15f, 0.08f),
        Triple(0.78f, 0.65f, 0.12f),
        Triple(0.35f, 0.78f, 0.09f),
        Triple(0.90f, 0.85f, 0.07f)
    )
    spots.forEach { (fx, fy, fr) ->
        drawCircle(
            color = MottleColor.copy(alpha = 0.35f),
            radius = w * fr,
            center = Offset(w * fx, h * fy)
        )
    }
}

private fun DrawScope.drawVein(
    w: Float,
    h: Float,
    points: List<Pair<Float, Float>>,
    color: Color,
    strokeWidth: Float,
    alpha: Float
) {
    if (points.size < 2) return
    val path = Path()
    val (x0, y0) = points.first()
    path.moveTo(w * x0, h * y0)
    for (i in 1 until points.size) {
        val (px, py) = points[i]
        val (prevX, prevY) = points[i - 1]
        val midX = w * (prevX + px) / 2
        val midY = h * (prevY + py) / 2
        path.quadraticTo(midX, midY, w * px, h * py)
    }
    drawPath(
        path = path,
        color = color.copy(alpha = alpha),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}
