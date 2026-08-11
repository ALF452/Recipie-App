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

private data class Plank(val index: Int, val startFraction: Float, val endFraction: Float, val baseColor: Color)

private data class GrainLine(
    val plankIndex: Int,
    val xInPlank: Float,
    val amplitudeInPlank: Float,
    val wavelength: Float,
    val phase: Float,
    val alpha: Float,
    val light: Boolean,
    val widthPx: Float
)

private data class Scratch(
    val xFraction: Float,
    val yFraction: Float,
    val lengthFraction: Float,
    val angleDegrees: Float,
    val alpha: Float
)

private data class Pore(val xFraction: Float, val yFraction: Float, val alpha: Float, val light: Boolean)

private val plankPalette = listOf(
    Color(0xFF9C6B3E),
    Color(0xFF8A5A32),
    Color(0xFFA5754A),
    Color(0xFF7C4F2B),
    Color(0xFF946338),
    Color(0xFF875C36)
)

private fun Color.blend(other: Color, fraction: Float): Color = Color(
    red = red + (other.red - red) * fraction,
    green = green + (other.green - green) * fraction,
    blue = blue + (other.blue - blue) * fraction,
    alpha = alpha
)

/**
 * Procedurally drawn background resembling a butcher-block cutting board: glued
 * vertical planks whose grain runs along their length (top to bottom, matching
 * how the strips were actually cut), each with a rounded, beveled edge so the
 * seams read as physical 3D ridges rather than flat stripes. Fine pore
 * texture, knife scratches and a soft sheen add surface detail; a top contact
 * shadow and edge vignette separate it visually from the marble counter above
 * it. Fully generated, no image assets required.
 */
@Composable
fun WoodenCuttingBoardBackground(modifier: Modifier = Modifier, seed: Int = 42) {
    val random = remember(seed) { Random(seed) }

    val plankCount = 9
    val planks = remember(seed) {
        val bounds = (0..plankCount).map { it / plankCount.toFloat() }
        bounds.zipWithNext().mapIndexed { i, (start, end) ->
            Plank(i, start, end, plankPalette[random.nextInt(plankPalette.size)])
        }
    }

    val grainLines = remember(seed) {
        planks.flatMap { plank ->
            List(9) {
                GrainLine(
                    plankIndex = plank.index,
                    xInPlank = random.nextFloat(),
                    amplitudeInPlank = 0.05f + random.nextFloat() * 0.10f,
                    wavelength = 1.1f + random.nextFloat() * 1.6f,
                    phase = random.nextFloat() * 6.28f,
                    alpha = 0.08f + random.nextFloat() * 0.16f,
                    light = random.nextBoolean(),
                    widthPx = 0.8f + random.nextFloat() * 1.0f
                )
            }
        }
    }

    val scratches = remember(seed) {
        List(26) {
            Scratch(
                xFraction = random.nextFloat(),
                yFraction = random.nextFloat(),
                lengthFraction = 0.03f + random.nextFloat() * 0.09f,
                angleDegrees = random.nextFloat() * 360f,
                alpha = 0.05f + random.nextFloat() * 0.09f
            )
        }
    }

    val pores = remember(seed) {
        List(140) {
            Pore(
                xFraction = random.nextFloat(),
                yFraction = random.nextFloat(),
                alpha = 0.03f + random.nextFloat() * 0.05f,
                light = random.nextBoolean()
            )
        }
    }

    Canvas(modifier = modifier) {
        drawWoodenBoard(planks, grainLines, scratches, pores)
    }
}

private fun DrawScope.drawWoodenBoard(
    planks: List<Plank>,
    grainLines: List<GrainLine>,
    scratches: List<Scratch>,
    pores: List<Pore>
) {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return

    for (plank in planks) {
        val left = plank.startFraction * w
        val right = plank.endFraction * w
        val edgeDark = plank.baseColor.blend(Color.Black, 0.42f)
        val edgeLight = plank.baseColor.blend(Color.White, 0.20f)
        drawRect(
            brush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0f to edgeDark,
                    0.06f to edgeLight,
                    0.18f to plank.baseColor,
                    0.82f to plank.baseColor,
                    0.94f to edgeLight,
                    1f to edgeDark
                ),
                startX = left,
                endX = right
            ),
            topLeft = Offset(left, 0f),
            size = Size(right - left, h)
        )
    }

    for (grain in grainLines) {
        val plank = planks[grain.plankIndex]
        val left = plank.startFraction * w
        val right = plank.endFraction * w
        val plankWidth = right - left
        val baseX = left + grain.xInPlank * plankWidth
        val path = Path()
        val steps = 20
        for (i in 0..steps) {
            val t = i / steps.toFloat()
            val y = t * h
            val x = baseX + sin(t * 6.28f * grain.wavelength + grain.phase) * (grain.amplitudeInPlank * plankWidth)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        val color = if (grain.light) {
            Color(0xFFFFE6C7).copy(alpha = grain.alpha * 0.6f)
        } else {
            Color.Black.copy(alpha = grain.alpha)
        }
        drawPath(path = path, color = color, style = Stroke(width = grain.widthPx))
    }

    for (pore in pores) {
        val color = if (pore.light) Color(0xFFFFE6C7) else Color(0xFF2A1808)
        drawCircle(
            color = color.copy(alpha = pore.alpha),
            radius = 0.6f,
            center = Offset(pore.xFraction * w, pore.yFraction * h)
        )
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

    // Soft diagonal sheen, as if lit from the upper-left.
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.05f), Color.Transparent),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        size = Size(w, h)
    )

    // Contact shadow along the top, where the board meets the counter above it.
    val topShadowHeight = (h * 0.05f).coerceAtMost(28f)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Black.copy(alpha = 0.30f), Color.Transparent),
            startY = 0f,
            endY = topShadowHeight
        ),
        size = Size(w, topShadowHeight)
    )

    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.32f)),
            center = Offset(w / 2f, h / 2f),
            radius = max(w, h) * 0.75f
        ),
        size = Size(w, h)
    )
}
