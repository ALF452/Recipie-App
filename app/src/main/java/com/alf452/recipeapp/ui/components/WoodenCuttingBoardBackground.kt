package com.alf452.recipeapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

private data class Plank(val startFraction: Float, val endFraction: Float, val baseColor: Color)

private data class GrainLine(
    val yFraction: Float,
    val amplitudeFraction: Float,
    val phase: Float,
    val alpha: Float,
    val light: Boolean
)

private data class Knot(val xFraction: Float, val yFraction: Float, val radiusFraction: Float)

private data class Scratch(
    val xFraction: Float,
    val yFraction: Float,
    val lengthFraction: Float,
    val angleDegrees: Float,
    val alpha: Float
)

private val plankPalette = listOf(
    Color(0xFF9C6B3E),
    Color(0xFF8A5A32),
    Color(0xFFA5754A),
    Color(0xFF7C4F2B),
    Color(0xFF946338),
    Color(0xFF875C36)
)

/**
 * Procedurally drawn background resembling a worn, stained wooden cutting board:
 * glued vertical planks, wood grain, a few knots, faint knife scratches, and a
 * darkened vignette at the edges. Fully generated, no image assets required.
 */
@Composable
fun WoodenCuttingBoardBackground(modifier: Modifier = Modifier, seed: Int = 42) {
    val random = remember(seed) { Random(seed) }

    val plankCount = 6
    val planks = remember(seed) {
        val bounds = (0..plankCount).map { it / plankCount.toFloat() }
        bounds.zipWithNext { start, end -> Plank(start, end, plankPalette[random.nextInt(plankPalette.size)]) }
    }

    val grainLines = remember(seed) {
        List(50) {
            GrainLine(
                yFraction = random.nextFloat(),
                amplitudeFraction = 0.003f + random.nextFloat() * 0.008f,
                phase = random.nextFloat() * 6.28f,
                alpha = 0.10f + random.nextFloat() * 0.16f,
                light = random.nextBoolean()
            )
        }
    }

    val knots = remember(seed) {
        List(4) {
            Knot(
                xFraction = random.nextFloat(),
                yFraction = random.nextFloat(),
                radiusFraction = 0.010f + random.nextFloat() * 0.014f
            )
        }
    }

    val scratches = remember(seed) {
        List(22) {
            Scratch(
                xFraction = random.nextFloat(),
                yFraction = random.nextFloat(),
                lengthFraction = 0.03f + random.nextFloat() * 0.09f,
                angleDegrees = random.nextFloat() * 360f,
                alpha = 0.05f + random.nextFloat() * 0.09f
            )
        }
    }

    Canvas(modifier = modifier) {
        drawWoodenBoard(planks, grainLines, knots, scratches)
    }
}

private fun DrawScope.drawWoodenBoard(
    planks: List<Plank>,
    grainLines: List<GrainLine>,
    knots: List<Knot>,
    scratches: List<Scratch>
) {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return

    for (plank in planks) {
        val left = plank.startFraction * w
        val right = plank.endFraction * w
        drawRect(
            color = plank.baseColor,
            topLeft = Offset(left, 0f),
            size = Size(right - left, h)
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.20f),
            topLeft = Offset(right - 1.5f, 0f),
            size = Size(3f, h)
        )
    }

    for (line in grainLines) {
        val baseY = line.yFraction * h
        val amplitude = line.amplitudeFraction * h
        val path = Path()
        val steps = 24
        for (i in 0..steps) {
            val t = i / steps.toFloat()
            val x = t * w
            val y = baseY + sin(t * 6.28f * 2f + line.phase) * amplitude
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        val color = if (line.light) {
            Color(0xFFFFE6C7).copy(alpha = line.alpha * 0.6f)
        } else {
            Color.Black.copy(alpha = line.alpha)
        }
        drawPath(path = path, color = color, style = Stroke(width = 1.2f))
    }

    for (knot in knots) {
        val center = Offset(knot.xFraction * w, knot.yFraction * h)
        val radius = knot.radiusFraction * w
        drawCircle(color = Color(0xFF4A2F18).copy(alpha = 0.32f), radius = radius, center = center)
        drawCircle(color = Color(0xFF3A2412).copy(alpha = 0.45f), radius = radius * 0.5f, center = center)
    }

    for (scratch in scratches) {
        val cx = scratch.xFraction * w
        val cy = scratch.yFraction * h
        val len = scratch.lengthFraction * w
        val rad = Math.toRadians(scratch.angleDegrees.toDouble())
        val dx = (cos(rad) * len / 2).toFloat()
        val dy = (sin(rad) * len / 2).toFloat()
        drawLine(
            color = Color(0xFFFFE6C7).copy(alpha = scratch.alpha),
            start = Offset(cx - dx, cy - dy),
            end = Offset(cx + dx, cy + dy),
            strokeWidth = 1f
        )
    }

    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.30f)),
            center = Offset(w / 2f, h / 2f),
            radius = max(w, h) * 0.75f
        ),
        size = Size(w, h)
    )
}
