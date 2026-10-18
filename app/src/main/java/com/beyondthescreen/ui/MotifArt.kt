package com.beyondthescreen.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.beyondthescreen.recommendation.Motif

data class MotifPalette(val field: Color, val accent: Color, val paper: Color)

fun motifPalette(motif: Motif): MotifPalette = when (motif) {
    Motif.SHIP -> MotifPalette(Color(0xFF142033), Color(0xFFE2B15A), Color(0xFFD5E4F2))
    Motif.REGENCY -> MotifPalette(Color(0xFF2A2228), Color(0xFFE7C1C8), Color(0xFFF3E6D8))
    Motif.WORMHOLE -> MotifPalette(Color(0xFF101820), Color(0xFFF4F7FB), Color(0xFF8FB9E8))
    Motif.CYMBAL -> MotifPalette(Color(0xFF1A140C), Color(0xFFE0B15A), Color(0xFFF7E7C1))
    Motif.HOTEL -> MotifPalette(Color(0xFF2C1C24), Color(0xFFF0B4C4), Color(0xFFF7E1C8))
    Motif.SUSHI -> MotifPalette(Color(0xFF1A1C22), Color(0xFFF4F1EA), Color(0xFFE07A5F))
    Motif.DRAGON -> MotifPalette(Color(0xFF2A1410), Color(0xFFE36A3A), Color(0xFFF0C9A0))
    Motif.BUNNY -> MotifPalette(Color(0xFF1A2418), Color(0xFFF0A35E), Color(0xFFD7E7C8))
    Motif.ROBOT -> MotifPalette(Color(0xFF1A1C24), Color(0xFF9AD7E0), Color(0xFFC5C9D4))
    Motif.MACHINE -> MotifPalette(Color(0xFF241E14), Color(0xFFD2A15E), Color(0xFFE6D7BE))
}

@Composable
fun MotifArt(motif: Motif, modifier: Modifier = Modifier) {
    val colors = motifPalette(motif)
    Canvas(modifier.background(colors.field)) {
        val stroke = Stroke(
            width = size.minDimension * 0.02f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        when (motif) {
            Motif.SHIP -> ship(colors, stroke)
            Motif.REGENCY -> regency(colors, stroke)
            Motif.WORMHOLE -> wormhole(colors, stroke)
            Motif.CYMBAL -> cymbal(colors, stroke)
            Motif.HOTEL -> hotel(colors, stroke)
            Motif.SUSHI -> sushi(colors, stroke)
            Motif.DRAGON -> dragon(colors, stroke)
            Motif.BUNNY -> bunny(colors, stroke)
            Motif.ROBOT -> robot(colors, stroke)
            Motif.MACHINE -> machine(colors, stroke)
        }
    }
}

private fun DrawScope.p(x: Float, y: Float) = Offset(size.width * x, size.height * y)

private fun DrawScope.ship(colors: MotifPalette, stroke: Stroke) {
    drawCircle(colors.accent, radius = size.minDimension * 0.06f, center = p(0.78f, 0.24f))
    drawLine(colors.paper.copy(alpha = 0.7f), p(0.1f, 0.72f), p(0.9f, 0.72f), strokeWidth = stroke.width)
    val hull = Path().apply {
        moveTo(p(0.16f, 0.7f).x, p(0.16f, 0.7f).y)
        quadraticTo(p(0.5f, 0.92f).x, p(0.5f, 0.92f).y, p(0.84f, 0.7f).x, p(0.84f, 0.7f).y)
    }
    drawPath(hull, colors.paper, style = stroke)
    drawLine(colors.paper, p(0.38f, 0.7f), p(0.38f, 0.28f), strokeWidth = stroke.width)
    drawLine(colors.paper, p(0.62f, 0.7f), p(0.62f, 0.36f), strokeWidth = stroke.width)
    val sail = Path().apply {
        moveTo(p(0.4f, 0.32f).x, p(0.4f, 0.32f).y)
        lineTo(p(0.4f, 0.64f).x, p(0.4f, 0.64f).y)
        lineTo(p(0.56f, 0.5f).x, p(0.56f, 0.5f).y)
        close()
    }
    drawPath(sail, colors.accent.copy(alpha = 0.9f))
}

private fun DrawScope.regency(colors: MotifPalette, stroke: Stroke) {
    drawLine(colors.paper, p(0.28f, 0.82f), p(0.28f, 0.28f), strokeWidth = stroke.width)
    drawLine(colors.paper, p(0.72f, 0.82f), p(0.72f, 0.28f), strokeWidth = stroke.width)
    val arch = Path().apply {
        moveTo(p(0.28f, 0.42f).x, p(0.28f, 0.42f).y)
        quadraticTo(p(0.5f, 0.12f).x, p(0.5f, 0.12f).y, p(0.72f, 0.42f).x, p(0.72f, 0.42f).y)
    }
    drawPath(arch, colors.accent, style = stroke)
    drawCircle(colors.accent.copy(alpha = 0.35f), radius = size.minDimension * 0.12f, center = p(0.5f, 0.62f))
}

private fun DrawScope.wormhole(colors: MotifPalette, stroke: Stroke) {
    listOf(0.34f, 0.26f, 0.18f, 0.1f).forEachIndexed { index, radius ->
        drawOval(
            color = if (index % 2 == 0) colors.paper else colors.accent,
            topLeft = p(0.5f - radius, 0.5f - radius * 0.62f) ,
            size = androidx.compose.ui.geometry.Size(size.width * radius * 2, size.height * radius * 1.24f),
            style = stroke,
        )
    }
    drawCircle(colors.accent, radius = size.minDimension * 0.035f, center = p(0.56f, 0.48f))
}

private fun DrawScope.cymbal(colors: MotifPalette, stroke: Stroke) {
    drawCircle(colors.accent.copy(alpha = 0.25f), radius = size.minDimension * 0.34f, center = p(0.48f, 0.52f))
    drawCircle(colors.paper, radius = size.minDimension * 0.26f, center = p(0.48f, 0.52f), style = stroke)
    drawCircle(colors.accent, radius = size.minDimension * 0.12f, center = p(0.48f, 0.52f), style = stroke)
    drawLine(colors.accent, p(0.7f, 0.22f), p(0.5f, 0.5f), strokeWidth = stroke.width * 1.3f)
}

private fun DrawScope.hotel(colors: MotifPalette, stroke: Stroke) {
    drawRoundRect(
        color = colors.paper,
        topLeft = p(0.28f, 0.28f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.44f, size.height * 0.54f),
        style = stroke,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
    )
    val awning = Path().apply {
        moveTo(p(0.24f, 0.4f).x, p(0.24f, 0.4f).y)
        lineTo(p(0.5f, 0.22f).x, p(0.5f, 0.22f).y)
        lineTo(p(0.76f, 0.4f).x, p(0.76f, 0.4f).y)
    }
    drawPath(awning, colors.accent, style = stroke)
    for (row in 0..1) {
        for (col in 0..2) {
            drawRect(
                colors.accent,
                topLeft = p(0.34f + col * 0.1f, 0.4f + row * 0.14f),
                size = androidx.compose.ui.geometry.Size(size.minDimension * 0.035f, size.minDimension * 0.045f),
            )
        }
    }
}

private fun DrawScope.sushi(colors: MotifPalette, stroke: Stroke) {
    drawOval(
        color = colors.paper,
        topLeft = p(0.16f, 0.38f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.68f, size.height * 0.36f),
        style = stroke,
    )
    drawRoundRect(
        color = colors.accent,
        topLeft = p(0.32f, 0.46f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.36f, size.height * 0.16f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f),
    )
    drawRect(
        color = colors.field,
        topLeft = p(0.32f, 0.5f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.36f, size.height * 0.045f),
    )
    drawCircle(colors.paper, radius = size.minDimension * 0.03f, center = p(0.62f, 0.52f))
}

private fun DrawScope.dragon(colors: MotifPalette, stroke: Stroke) {
    val wing = Path().apply {
        moveTo(p(0.22f, 0.62f).x, p(0.22f, 0.62f).y)
        quadraticTo(p(0.4f, 0.18f).x, p(0.4f, 0.18f).y, p(0.78f, 0.42f).x, p(0.78f, 0.42f).y)
        quadraticTo(p(0.5f, 0.48f).x, p(0.5f, 0.48f).y, p(0.3f, 0.7f).x, p(0.3f, 0.7f).y)
    }
    drawPath(wing, colors.accent.copy(alpha = 0.85f))
    drawPath(wing, colors.paper, style = stroke)
    drawCircle(colors.paper, radius = size.minDimension * 0.035f, center = p(0.7f, 0.4f))
    drawCircle(colors.accent, radius = size.minDimension * 0.025f, center = p(0.24f, 0.78f))
    drawCircle(colors.accent, radius = size.minDimension * 0.018f, center = p(0.34f, 0.84f))
}

private fun DrawScope.bunny(colors: MotifPalette, stroke: Stroke) {
    drawOval(colors.paper, p(0.38f, 0.12f), androidx.compose.ui.geometry.Size(size.width * 0.1f, size.height * 0.28f), style = stroke)
    drawOval(colors.paper, p(0.54f, 0.12f), androidx.compose.ui.geometry.Size(size.width * 0.1f, size.height * 0.28f), style = stroke)
    drawCircle(colors.paper, radius = size.minDimension * 0.16f, center = p(0.5f, 0.48f), style = stroke)
    drawCircle(colors.accent.copy(alpha = 0.9f), radius = size.minDimension * 0.2f, center = p(0.5f, 0.7f))
    drawCircle(colors.accent, radius = size.minDimension * 0.05f, center = p(0.72f, 0.74f))
}

private fun DrawScope.robot(colors: MotifPalette, stroke: Stroke) {
    drawRoundRect(
        colors.paper,
        topLeft = p(0.3f, 0.28f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.4f, size.height * 0.32f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f),
        style = stroke,
    )
    drawLine(colors.accent, p(0.36f, 0.44f), p(0.64f, 0.44f), strokeWidth = stroke.width * 1.6f)
    drawLine(colors.paper, p(0.5f, 0.28f), p(0.5f, 0.14f), strokeWidth = stroke.width)
    drawCircle(colors.accent, radius = size.minDimension * 0.03f, center = p(0.5f, 0.12f))
    drawLine(colors.paper, p(0.22f, 0.7f), p(0.78f, 0.7f), strokeWidth = stroke.width)
    drawLine(colors.paper, p(0.28f, 0.6f), p(0.28f, 0.8f), strokeWidth = stroke.width)
    drawLine(colors.paper, p(0.72f, 0.6f), p(0.72f, 0.8f), strokeWidth = stroke.width)
}

private fun DrawScope.machine(colors: MotifPalette, stroke: Stroke) {
    drawCircle(colors.paper, radius = size.minDimension * 0.28f, center = p(0.46f, 0.52f), style = stroke)
    drawCircle(colors.accent, radius = size.minDimension * 0.12f, center = p(0.46f, 0.52f), style = stroke)
    drawCircle(colors.accent, radius = size.minDimension * 0.16f, center = p(0.7f, 0.34f), style = stroke)
    drawCircle(colors.paper, radius = size.minDimension * 0.05f, center = p(0.7f, 0.34f))
    drawLine(colors.paper, p(0.58f, 0.42f), p(0.46f, 0.52f), strokeWidth = stroke.width)
}
