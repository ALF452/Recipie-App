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
import kotlin.math.cos
import kotlin.math.sin

private val SkyTop = Color(0xFF6EC0E8)
private val SkyHorizon = Color(0xFFDCF3FF)
private val SunColor = Color(0xFFFFD54F)
private val GrassGreen = Color(0xFF7CB342)
private val GrassShadow = Color(0xFF5C8A2E)
private val TreeTrunk = Color(0xFF6D4C41)
private val TreeFoliage = Color(0xFF66BB6A)
private val PondBlue = Color(0xFF4FC3F7)
private val PondDeep = Color(0xFF29ABE2)
private val DuckBody = Color(0xFFFFFDE7)
private val DuckBeak = Color(0xFFFFA000)
private val WindowFrame = Color(0xFFFAF6EE)
private val WindowFrameShadow = Color(0x22000000)
private val SillWood = Color(0xFF6D4C41)
private val SillWoodHighlight = Color(0xFF8D6247)
private val PotColor = Color(0xFFBF6F4A)
private val PotRim = Color(0xFFA85736)
private val LeafGreen = Color(0xFF4C8C4A)
private val LeafGreenLight = Color(0xFF6FAF57)

/**
 * A window looking out onto a sunny park with a duck pond, with a row of
 * herb pots growing on the sill beneath the glass. Replaces a flat color
 * banner behind the main menu's top app bar. Fully procedural (Canvas),
 * no image assets.
 */
@Composable
fun WindowHerbGardenBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawScene()
    }
}

private fun DrawScope.drawScene() {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return

    val sillTop = h * 0.74f

    drawSky(w, sillTop)
    drawSun(w, sillTop)
    drawGrassAndTrees(w, sillTop)
    drawPondAndDucks(w, sillTop)
    drawWindowFrame(w, sillTop)
    drawSill(w, h, sillTop)
    drawHerbPots(w, h, sillTop)
}

private fun DrawScope.drawSky(w: Float, sillTop: Float) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(SkyTop, SkyHorizon),
            startY = 0f,
            endY = sillTop
        ),
        size = Size(w, sillTop)
    )
}

private fun DrawScope.drawSun(w: Float, sillTop: Float) {
    val center = Offset(w * 0.16f, sillTop * 0.28f)
    val baseRadius = w * 0.045f
    listOf(3.2f to 0.10f, 2.1f to 0.16f, 1.4f to 0.22f).forEach { (mult, alpha) ->
        drawCircle(color = SunColor.copy(alpha = alpha), radius = baseRadius * mult, center = center)
    }
    drawCircle(color = SunColor, radius = baseRadius, center = center)
}

private fun DrawScope.drawGrassAndTrees(w: Float, sillTop: Float) {
    val grassTop = sillTop * 0.62f
    drawRect(
        color = GrassGreen,
        topLeft = Offset(0f, grassTop),
        size = Size(w, sillTop - grassTop)
    )
    drawLine(
        color = GrassShadow,
        start = Offset(0f, grassTop),
        end = Offset(w, grassTop),
        strokeWidth = 2f
    )

    val treeXs = listOf(w * 0.10f, w * 0.85f)
    treeXs.forEach { tx ->
        val trunkHeight = sillTop * 0.10f
        drawLine(
            color = TreeTrunk,
            start = Offset(tx, grassTop),
            end = Offset(tx, grassTop - trunkHeight),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        drawCircle(color = TreeFoliage, radius = sillTop * 0.075f, center = Offset(tx, grassTop - trunkHeight - sillTop * 0.03f))
    }
}

private fun DrawScope.drawPondAndDucks(w: Float, sillTop: Float) {
    val pondCenter = Offset(w * 0.56f, sillTop * 0.86f)
    val pondSize = Size(w * 0.34f, sillTop * 0.16f)

    drawOval(
        color = PondBlue,
        topLeft = Offset(pondCenter.x - pondSize.width / 2, pondCenter.y - pondSize.height / 2),
        size = pondSize
    )
    drawOval(
        color = PondDeep.copy(alpha = 0.5f),
        topLeft = Offset(pondCenter.x - pondSize.width * 0.3f, pondCenter.y - pondSize.height * 0.15f),
        size = Size(pondSize.width * 0.6f, pondSize.height * 0.5f)
    )

    drawDuck(Offset(pondCenter.x - pondSize.width * 0.18f, pondCenter.y - pondSize.height * 0.05f), pondSize.height * 0.85f)
    drawDuck(Offset(pondCenter.x + pondSize.width * 0.22f, pondCenter.y + pondSize.height * 0.12f), pondSize.height * 0.7f)
}

private fun DrawScope.drawDuck(center: Offset, scale: Float) {
    val bodyWidth = scale * 1.6f
    val bodyHeight = scale * 0.85f

    drawOval(
        color = DuckBody,
        topLeft = Offset(center.x - bodyWidth / 2, center.y - bodyHeight / 2),
        size = Size(bodyWidth, bodyHeight)
    )

    val headCenter = Offset(center.x + bodyWidth * 0.32f, center.y - bodyHeight * 0.55f)
    drawCircle(color = DuckBody, radius = scale * 0.4f, center = headCenter)

    val beakPath = Path().apply {
        moveTo(headCenter.x + scale * 0.32f, headCenter.y)
        lineTo(headCenter.x + scale * 0.62f, headCenter.y - scale * 0.06f)
        lineTo(headCenter.x + scale * 0.32f, headCenter.y + scale * 0.18f)
        close()
    }
    drawPath(beakPath, color = DuckBeak)

    drawCircle(color = Color.Black, radius = scale * 0.06f, center = Offset(headCenter.x + scale * 0.1f, headCenter.y - scale * 0.08f))
}

private fun DrawScope.drawWindowFrame(w: Float, sillTop: Float) {
    val frameThickness = w * 0.02f

    // outer frame border
    drawRect(
        color = WindowFrame,
        topLeft = Offset.Zero,
        size = Size(w, sillTop),
        style = Stroke(width = frameThickness)
    )

    // mullions (cross bars dividing the glass into four panes)
    drawLine(
        color = WindowFrame,
        start = Offset(w / 2f, frameThickness / 2),
        end = Offset(w / 2f, sillTop - frameThickness / 2),
        strokeWidth = frameThickness * 0.8f
    )
    drawLine(
        color = WindowFrame,
        start = Offset(frameThickness / 2, sillTop / 2f),
        end = Offset(w - frameThickness / 2, sillTop / 2f),
        strokeWidth = frameThickness * 0.8f
    )

    // subtle inner shadow for depth right where the frame meets the glass
    drawRect(
        color = WindowFrameShadow,
        topLeft = Offset(frameThickness, frameThickness),
        size = Size(w - frameThickness * 2, sillTop - frameThickness * 2),
        style = Stroke(width = frameThickness * 0.35f)
    )
}

private fun DrawScope.drawSill(w: Float, h: Float, sillTop: Float) {
    drawRect(
        color = SillWood,
        topLeft = Offset(0f, sillTop),
        size = Size(w, h - sillTop)
    )
    drawLine(
        color = SillWoodHighlight,
        start = Offset(0f, sillTop + 2f),
        end = Offset(w, sillTop + 2f),
        strokeWidth = 3f
    )
}

private fun DrawScope.drawHerbPots(w: Float, h: Float, sillTop: Float) {
    val potCount = 4
    val potWidth = w / (potCount * 2.4f)
    val potHeight = (h - sillTop) * 0.62f
    val baseY = sillTop + (h - sillTop) * 0.78f

    for (i in 0 until potCount) {
        val cx = w * (i + 0.5f) / potCount
        val leafColor = if (i % 2 == 0) LeafGreen else LeafGreenLight
        drawHerbPot(cx, baseY, potWidth, potHeight, leafColor)
    }
}

private fun DrawScope.drawHerbPot(baseX: Float, baseY: Float, potWidth: Float, potHeight: Float, leafColor: Color) {
    val potTopY = baseY - potHeight

    val potPath = Path().apply {
        moveTo(baseX - potWidth * 0.35f, baseY)
        lineTo(baseX + potWidth * 0.35f, baseY)
        lineTo(baseX + potWidth * 0.5f, potTopY)
        lineTo(baseX - potWidth * 0.5f, potTopY)
        close()
    }
    drawPath(potPath, color = PotColor)
    drawLine(
        color = PotRim,
        start = Offset(baseX - potWidth * 0.5f, potTopY),
        end = Offset(baseX + potWidth * 0.5f, potTopY),
        strokeWidth = potWidth * 0.12f
    )

    val sprigAngles = listOf(-55f, -20f, 15f, 50f)
    val sprigLength = potHeight * 0.9f
    sprigAngles.forEach { angleDeg ->
        val rad = Math.toRadians((angleDeg - 90f).toDouble())
        val tip = Offset(
            baseX + (cos(rad) * sprigLength).toFloat(),
            potTopY + (sin(rad) * sprigLength).toFloat()
        )
        drawLine(
            color = leafColor,
            start = Offset(baseX, potTopY),
            end = tip,
            strokeWidth = potWidth * 0.09f,
            cap = StrokeCap.Round
        )
        drawCircle(color = leafColor, radius = potWidth * 0.16f, center = tip)
    }
}
