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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

// Cool near-white base (real Carrara/Calacatta marble is closer to neutral
// white-grey than warm cream) with thin, sharp, branching fracture veins
// instead of thick smooth curves — the latter is what reads as "cartoon."
private val MarbleLight = Color(0xFFF7F6F3)
private val MarbleShade = Color(0xFFEEEDE8)
private val MottleColor = Color(0xFFDEDBD3)
private val VeinPrimary = Color(0xFF827C71)
private val VeinSecondary = Color(0xFFA39D91)
private val VeinFine = Color(0xFFBDB8AC)
private val VeinWarm = Color(0xFF9C8863)

/**
 * A white marble countertop behind the main menu's top app bar, paired with
 * the wooden cutting board that fills the rest of the screen. Modeled as a
 * network of thin, angular, branching fracture veins (how real marble
 * veining actually reads) rather than a few thick smooth curves, which is
 * what made the first attempt look illustrated instead of like stone. Fully
 * procedural (Canvas), no image assets.
 */
@Composable
fun MarbleCountertopBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawMarble()
    }
}

private data class Vein(val points: List<Pair<Float, Float>>, val color: Color, val strokeWidth: Float, val alpha: Float)

private fun DrawScope.drawMarble() {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return

    drawRect(
        brush = Brush.verticalGradient(colors = listOf(MarbleLight, MarbleShade), startY = 0f, endY = h),
        size = Size(w, h)
    )

    drawMottling(w, h)

    val veins = listOf(
        // primary crack, upper-left to lower-middle
        Vein(listOf(0.04f to 0.04f, 0.14f to 0.20f, 0.12f to 0.36f, 0.24f to 0.54f, 0.20f to 0.74f, 0.28f to 0.97f), VeinPrimary, 1.4f, 0.6f),
        // branch off the primary, forking upper-right
        Vein(listOf(0.14f to 0.20f, 0.27f to 0.16f, 0.39f to 0.09f), VeinSecondary, 0.9f, 0.45f),
        // branch off the primary, mid
        Vein(listOf(0.24f to 0.54f, 0.37f to 0.59f, 0.47f to 0.50f), VeinSecondary, 0.8f, 0.4f),
        // fine sub-branch
        Vein(listOf(0.37f to 0.59f, 0.44f to 0.66f), VeinFine, 0.5f, 0.3f),
        // branch off the primary, lower-left
        Vein(listOf(0.20f to 0.74f, 0.09f to 0.84f, 0.03f to 0.95f), VeinFine, 0.6f, 0.32f),

        // primary crack, upper-right
        Vein(listOf(0.56f to 0.04f, 0.63f to 0.17f, 0.59f to 0.31f, 0.69f to 0.41f, 0.66f to 0.58f), VeinPrimary, 1.3f, 0.55f),
        Vein(listOf(0.63f to 0.17f, 0.76f to 0.09f, 0.89f to 0.14f), VeinSecondary, 0.85f, 0.4f),
        Vein(listOf(0.69f to 0.41f, 0.81f to 0.37f, 0.93f to 0.44f), VeinSecondary, 0.8f, 0.38f),

        // lower-right crack
        Vein(listOf(0.70f to 0.60f, 0.80f to 0.70f, 0.77f to 0.86f, 0.90f to 0.93f), VeinPrimary, 1.2f, 0.5f),
        Vein(listOf(0.80f to 0.70f, 0.92f to 0.65f, 0.98f to 0.73f), VeinFine, 0.55f, 0.3f),

        // isolated fine crack, lower-left
        Vein(listOf(0.02f to 0.60f, 0.08f to 0.68f, 0.06f to 0.80f), VeinFine, 0.5f, 0.28f),

        // subtle warm accent vein, near center (the faint gold-grey real
        // marble often shows alongside the cooler grey veining)
        Vein(listOf(0.42f to 0.28f, 0.48f to 0.38f, 0.46f to 0.50f), VeinWarm, 0.6f, 0.25f)
    )

    veins.forEach { drawCrack(w, h, it.points, it.color, it.alpha, it.strokeWidth) }
}

private fun DrawScope.drawMottling(w: Float, h: Float) {
    val spots = listOf(
        Triple(0.10f, 0.18f, 0.045f),
        Triple(0.30f, 0.45f, 0.035f),
        Triple(0.52f, 0.12f, 0.04f),
        Triple(0.63f, 0.55f, 0.05f),
        Triple(0.80f, 0.25f, 0.035f),
        Triple(0.86f, 0.72f, 0.045f),
        Triple(0.20f, 0.85f, 0.03f),
        Triple(0.94f, 0.90f, 0.03f)
    )
    spots.forEach { (fx, fy, fr) ->
        drawCircle(
            color = MottleColor.copy(alpha = 0.16f),
            radius = w * fr,
            center = Offset(w * fx, h * fy)
        )
    }
}

private fun DrawScope.drawCrack(
    w: Float,
    h: Float,
    points: List<Pair<Float, Float>>,
    color: Color,
    alpha: Float,
    strokeWidth: Float
) {
    if (points.size < 2) return
    val path = Path()
    val (x0, y0) = points.first()
    path.moveTo(w * x0, h * y0)
    for (i in 1 until points.size) {
        val (px, py) = points[i]
        path.lineTo(w * px, h * py)
    }
    drawPath(
        path = path,
        color = color.copy(alpha = alpha),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt, join = StrokeJoin.Round)
    )
}
