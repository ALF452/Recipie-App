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

// Palette drawn from Van Gogh's "Almond Blossom": deep cerulean sky worked in
// visible directional strokes, dark branch silhouettes, white and blush-pink
// blossom dabs, bold Japanese-print-style outlines.
private val SkyDeep = Color(0xFF1B5FA6)
private val SkyLight = Color(0xFF5AA0D8)
private val SkyStroke1 = Color(0xFF2E6DB4)
private val SkyStroke2 = Color(0xFF6FB0DB)
private val SkyStroke3 = Color(0xFF14477E)
private val SkyStroke4 = Color(0xFF8FC4E8)

private val SunCore = Color(0xFFFCE178)
private val SunMid = Color(0xFFF2A93B)
private val SunGlow = Color(0xFFF4C542)

private val BranchColor = Color(0xFF2B1B12)
private val BlossomWhite = Color(0xFFFFFBF2)
private val BlossomPink = Color(0xFFEFA9C0)
private val BlossomCenter = Color(0xFF8B5E3C)

private val GrassBase = Color(0xFF5D7F2E)
private val GrassStroke1 = Color(0xFF7FA33F)
private val GrassStroke2 = Color(0xFF3F5E1E)
private val GrassStroke3 = Color(0xFF9CBE55)
private val GrassOutline = Color(0xFF2B3F16)

private val PondBlue = Color(0xFF2E6DB4)
private val PondHighlight = Color(0xFFBFE1F5)
private val PondOutline = Color(0xFF12395F)

private val DuckBody = Color(0xFFFFF8E7)
private val DuckBeak = Color(0xFFE8871E)
private val DuckOutline = Color(0xFF2B1B12)

private val WindowFrame = Color(0xFFF5EFDD)
private val WindowOutline = Color(0xFF2B1B12)

private val SillWood = Color(0xFF6D4C41)
private val SillGrainDark = Color(0xFF4A3025)
private val SillGrainLight = Color(0xFF8A6650)

private val PotColor = Color(0xFFC1652E)
private val PotOutline = Color(0xFF2B1B12)
private val LeafSage = Color(0xFF6B8E3A)
private val LeafOlive = Color(0xFF7FA33F)

/**
 * A window looking out on a sunny park, painted in short directional strokes
 * rather than flat cartoon fills — deep cerulean sky, almond-blossom-style
 * branches with dabbed blossom clusters, and bold dark outlines, in the
 * spirit of Van Gogh's "Almond Blossom." Herb pots grow on the sill beneath
 * the glass. Fully procedural (Canvas), no image assets.
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
    drawGrassAndBranches(w, sillTop)
    drawPondAndDucks(w, sillTop)
    drawWindowFrame(w, sillTop)
    drawSill(w, h, sillTop)
    drawHerbPots(w, h, sillTop)
}

private fun DrawScope.drawSky(w: Float, sillTop: Float) {
    drawRect(
        brush = Brush.verticalGradient(colors = listOf(SkyDeep, SkyLight), startY = 0f, endY = sillTop),
        size = Size(w, sillTop)
    )

    val strokeColors = listOf(SkyStroke1, SkyStroke2, SkyStroke3, SkyStroke4)
    val rows = 6
    val cols = 9
    val rowHeight = sillTop / rows
    for (row in 0 until rows) {
        val y = rowHeight * (row + 0.5f)
        for (col in 0 until cols) {
            val x = w * (col + 0.5f) / cols
            val color = strokeColors[(row + col) % strokeColors.size]
            val angleDeg = if ((row + col) % 2 == 0) 22f else -16f
            val len = (w / cols) * 0.95f
            val rad = Math.toRadians(angleDeg.toDouble())
            val dx = (cos(rad) * len / 2).toFloat()
            val dy = (sin(rad) * len / 2).toFloat()
            drawLine(
                color = color.copy(alpha = 0.5f),
                start = Offset(x - dx, y - dy),
                end = Offset(x + dx, y + dy),
                strokeWidth = rowHeight * 0.55f,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawSun(w: Float, sillTop: Float) {
    val center = Offset(w * 0.20f, sillTop * 0.22f)
    val baseRadius = w * 0.05f

    drawCircle(color = SunGlow.copy(alpha = 0.20f), radius = baseRadius * 2.6f, center = Offset(center.x + 2f, center.y - 1f))
    drawCircle(color = SunGlow.copy(alpha = 0.30f), radius = baseRadius * 1.8f, center = Offset(center.x - 1f, center.y + 1f))

    val rayAngles = listOf(15f, 55f, 95f, 135f, 175f, 215f, 255f, 295f, 335f)
    rayAngles.forEach { deg ->
        val rad = Math.toRadians(deg.toDouble())
        val start = Offset(
            center.x + (cos(rad) * baseRadius * 1.25).toFloat(),
            center.y + (sin(rad) * baseRadius * 1.25).toFloat()
        )
        val end = Offset(
            center.x + (cos(rad) * baseRadius * 2.15).toFloat(),
            center.y + (sin(rad) * baseRadius * 2.15).toFloat()
        )
        drawLine(color = SunMid.copy(alpha = 0.55f), start = start, end = end, strokeWidth = baseRadius * 0.16f, cap = StrokeCap.Round)
    }

    drawCircle(color = SunMid, radius = baseRadius * 1.12f, center = center)
    drawCircle(color = SunCore, radius = baseRadius, center = center)
}

private fun DrawScope.drawGrassAndBranches(w: Float, sillTop: Float) {
    val grassTop = sillTop * 0.60f
    drawRect(color = GrassBase, topLeft = Offset(0f, grassTop), size = Size(w, sillTop - grassTop))

    val strokeColors = listOf(GrassStroke1, GrassStroke2, GrassStroke3)
    val cols = 16
    for (col in 0 until cols) {
        val x = w * (col + 0.5f) / cols
        val color = strokeColors[col % strokeColors.size]
        val topY = grassTop + (sillTop - grassTop) * (0.15f + (col % 3) * 0.12f)
        drawLine(
            color = color.copy(alpha = 0.6f),
            start = Offset(x, sillTop),
            end = Offset(x + (if (col % 2 == 0) 3f else -3f), topY),
            strokeWidth = (sillTop - grassTop) * 0.10f,
            cap = StrokeCap.Round
        )
    }
    drawLine(color = GrassOutline, start = Offset(0f, grassTop), end = Offset(w, grassTop), strokeWidth = 1.6f)

    drawAlmondBranch(Offset(w * 0.08f, grassTop), sillTop * 0.62f, mirror = false)
    drawAlmondBranch(Offset(w * 0.90f, grassTop), sillTop * 0.58f, mirror = true)
}

private fun DrawScope.drawAlmondBranch(origin: Offset, scale: Float, mirror: Boolean) {
    val dir = if (mirror) -1f else 1f

    val branchPath = Path().apply {
        moveTo(origin.x, origin.y)
        cubicTo(
            origin.x + dir * scale * 0.12f, origin.y - scale * 0.32f,
            origin.x + dir * scale * 0.04f, origin.y - scale * 0.55f,
            origin.x + dir * scale * 0.24f, origin.y - scale * 0.82f
        )
    }
    drawPath(branchPath, color = BranchColor, style = Stroke(width = scale * 0.05f, cap = StrokeCap.Round))

    val twigPath = Path().apply {
        moveTo(origin.x + dir * scale * 0.09f, origin.y - scale * 0.46f)
        cubicTo(
            origin.x + dir * scale * 0.27f, origin.y - scale * 0.5f,
            origin.x + dir * scale * 0.34f, origin.y - scale * 0.64f,
            origin.x + dir * scale * 0.44f, origin.y - scale * 0.72f
        )
    }
    drawPath(twigPath, color = BranchColor, style = Stroke(width = scale * 0.032f, cap = StrokeCap.Round))

    val blossomSpots = listOf(
        Offset(origin.x + dir * scale * 0.24f, origin.y - scale * 0.82f),
        Offset(origin.x + dir * scale * 0.13f, origin.y - scale * 0.52f),
        Offset(origin.x + dir * scale * 0.44f, origin.y - scale * 0.72f),
        Offset(origin.x + dir * scale * 0.02f, origin.y - scale * 0.28f),
        Offset(origin.x + dir * scale * 0.30f, origin.y - scale * 0.60f)
    )
    blossomSpots.forEachIndexed { i, spot ->
        val petal = if (i % 2 == 0) BlossomWhite else BlossomPink
        val r = scale * 0.055f
        drawCircle(color = petal, radius = r, center = Offset(spot.x - r * 0.6f, spot.y))
        drawCircle(color = petal, radius = r, center = Offset(spot.x + r * 0.6f, spot.y - r * 0.3f))
        drawCircle(color = petal.copy(alpha = 0.95f), radius = r * 0.9f, center = Offset(spot.x, spot.y + r * 0.7f))
        drawCircle(color = BlossomCenter, radius = r * 0.28f, center = spot)
    }
}

private fun DrawScope.drawPondAndDucks(w: Float, sillTop: Float) {
    val pondCenter = Offset(w * 0.56f, sillTop * 0.86f)
    val pondSize = Size(w * 0.34f, sillTop * 0.16f)
    val pondTopLeft = Offset(pondCenter.x - pondSize.width / 2, pondCenter.y - pondSize.height / 2)

    drawOval(color = PondBlue, topLeft = pondTopLeft, size = pondSize)
    drawOval(color = PondOutline, topLeft = pondTopLeft, size = pondSize, style = Stroke(width = 1.6f))

    drawLine(
        color = PondHighlight.copy(alpha = 0.7f),
        start = Offset(pondTopLeft.x + pondSize.width * 0.2f, pondCenter.y - pondSize.height * 0.1f),
        end = Offset(pondTopLeft.x + pondSize.width * 0.55f, pondCenter.y - pondSize.height * 0.05f),
        strokeWidth = pondSize.height * 0.14f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = PondHighlight.copy(alpha = 0.5f),
        start = Offset(pondTopLeft.x + pondSize.width * 0.35f, pondCenter.y + pondSize.height * 0.2f),
        end = Offset(pondTopLeft.x + pondSize.width * 0.75f, pondCenter.y + pondSize.height * 0.22f),
        strokeWidth = pondSize.height * 0.1f,
        cap = StrokeCap.Round
    )

    drawDuck(Offset(pondCenter.x - pondSize.width * 0.18f, pondCenter.y - pondSize.height * 0.05f), pondSize.height * 0.85f)
    drawDuck(Offset(pondCenter.x + pondSize.width * 0.22f, pondCenter.y + pondSize.height * 0.12f), pondSize.height * 0.7f)
}

private fun DrawScope.drawDuck(center: Offset, scale: Float) {
    val bodyWidth = scale * 1.6f
    val bodyHeight = scale * 0.85f
    val bodyTopLeft = Offset(center.x - bodyWidth / 2, center.y - bodyHeight / 2)

    drawOval(color = DuckBody, topLeft = bodyTopLeft, size = Size(bodyWidth, bodyHeight))
    drawOval(color = DuckOutline, topLeft = bodyTopLeft, size = Size(bodyWidth, bodyHeight), style = Stroke(width = 1.1f))

    val headCenter = Offset(center.x + bodyWidth * 0.32f, center.y - bodyHeight * 0.55f)
    drawCircle(color = DuckBody, radius = scale * 0.4f, center = headCenter)
    drawCircle(color = DuckOutline, radius = scale * 0.4f, center = headCenter, style = Stroke(width = 1f))

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
    val halfFrame = frameThickness / 2f
    val outlineExtra = frameThickness * 0.7f

    // Outline and fill share the same centerline; the wider outline stroke
    // peeks out on both sides of the narrower fill stroke drawn on top of it,
    // giving a bordered look instead of being fully covered.
    val frameTopLeft = Offset(halfFrame, halfFrame)
    val frameSize = Size(w - frameThickness, sillTop - frameThickness)
    drawRect(color = WindowOutline, topLeft = frameTopLeft, size = frameSize, style = Stroke(width = frameThickness + outlineExtra))
    drawRect(color = WindowFrame, topLeft = frameTopLeft, size = frameSize, style = Stroke(width = frameThickness))

    val mullionV = Offset(w / 2f, frameThickness / 2) to Offset(w / 2f, sillTop - frameThickness / 2)
    drawLine(color = WindowOutline, start = mullionV.first, end = mullionV.second, strokeWidth = frameThickness * 0.75f + outlineExtra)
    drawLine(color = WindowFrame, start = mullionV.first, end = mullionV.second, strokeWidth = frameThickness * 0.75f)

    val mullionH = Offset(frameThickness / 2, sillTop / 2f) to Offset(w - frameThickness / 2, sillTop / 2f)
    drawLine(color = WindowOutline, start = mullionH.first, end = mullionH.second, strokeWidth = frameThickness * 0.75f + outlineExtra)
    drawLine(color = WindowFrame, start = mullionH.first, end = mullionH.second, strokeWidth = frameThickness * 0.75f)
}

private fun DrawScope.drawSill(w: Float, h: Float, sillTop: Float) {
    drawRect(color = SillWood, topLeft = Offset(0f, sillTop), size = Size(w, h - sillTop))

    val grainColors = listOf(SillGrainDark, SillGrainLight)
    val lines = 5
    for (i in 0 until lines) {
        val y = sillTop + (h - sillTop) * (i + 1f) / (lines + 1f)
        drawLine(
            color = grainColors[i % grainColors.size].copy(alpha = 0.4f),
            start = Offset(w * 0.02f, y),
            end = Offset(w * 0.98f, y + (if (i % 2 == 0) 2f else -2f)),
            strokeWidth = 1.4f
        )
    }
    drawLine(color = SillGrainLight, start = Offset(0f, sillTop + 2f), end = Offset(w, sillTop + 2f), strokeWidth = 3f)
}

private fun DrawScope.drawHerbPots(w: Float, h: Float, sillTop: Float) {
    val potCount = 4
    val potWidth = w / (potCount * 2.4f)
    val potHeight = (h - sillTop) * 0.62f
    val baseY = sillTop + (h - sillTop) * 0.78f

    for (i in 0 until potCount) {
        val cx = w * (i + 0.5f) / potCount
        val leafColor = if (i % 2 == 0) LeafSage else LeafOlive
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
    drawPath(potPath, color = PotOutline, style = Stroke(width = potWidth * 0.06f))
    drawLine(
        color = PotOutline,
        start = Offset(baseX - potWidth * 0.5f, potTopY),
        end = Offset(baseX + potWidth * 0.5f, potTopY),
        strokeWidth = potWidth * 0.12f
    )

    val sprigAngles = listOf(-55f, -20f, 15f, 50f)
    val sprigLength = potHeight * 0.9f
    sprigAngles.forEachIndexed { idx, angleDeg ->
        val rad = Math.toRadians((angleDeg - 90f).toDouble())
        val tip = Offset(
            baseX + (cos(rad) * sprigLength).toFloat(),
            potTopY + (sin(rad) * sprigLength).toFloat()
        )
        val strokeColor = if (idx % 2 == 0) leafColor else leafColor.copy(alpha = 0.85f)
        drawLine(
            color = strokeColor,
            start = Offset(baseX, potTopY),
            end = tip,
            strokeWidth = potWidth * 0.1f,
            cap = StrokeCap.Round
        )
        drawCircle(color = strokeColor, radius = potWidth * 0.17f, center = tip)
    }
}
