package com.example.myhondafit.screens

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.example.myhondafit.nocturne.N
import kotlin.math.max

/**
 * The design's map is an inline SVG drawn in a 900×520 viewBox with
 * `preserveAspectRatio="xMidYMid slice"`. Redrawn here with the same geometry so
 * the home card and the map screen share one illustration, as they do in the design.
 */
@Composable
fun MapCanvas(modifier: Modifier = Modifier) {
    // The illustration deliberately overdraws its viewBox ("slice"), so it has
    // to be clipped — a Canvas does not clip to its bounds on its own.
    Canvas(modifier.clipToBounds()) {
        // "slice" = cover: scale to fill, centre the overflow.
        val s = max(size.width / VB_W, size.height / VB_H)
        val dx = (size.width - VB_W * s) / 2f
        val dy = (size.height - VB_H * s) / 2f

        drawRect(GROUND)

        translate(dx, dy) {
            scale(s, pivot = Offset.Zero) {
                drawMap()
            }
        }
    }
}

private const val VB_W = 900f
private const val VB_H = 520f

private val GROUND = Color(0xFF1C1E2B)
private val WATER = Color(0xFF1F2236)
private val PARK = Color(0xFF1E2A2A)
private val ROAD_MAJOR = Color(0xFF3A3D4C)
private val ROAD_MINOR = Color(0xFF33364A)

private fun DrawScope.drawMap() {
    // Ground fill beyond the viewBox edges, so "slice" overflow stays painted.
    drawRect(GROUND, topLeft = Offset(-60f, -60f), size = Size(VB_W + 120f, VB_H + 120f))

    // Riverbank along the bottom.
    drawPath(
        Path().apply {
            moveTo(-20f, 470f)
            cubicTo(180f, 430f, 260f, 500f, 520f, 460f)
            // SVG `S 820 420, 920 450` — first control is the reflection of the
            // previous one about the current point.
            cubicTo(780f, 420f, 820f, 420f, 920f, 450f)
            lineTo(920f, 540f)
            lineTo(-20f, 540f)
            close()
        },
        WATER,
    )

    // Park block.
    drawPath(
        Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    Rect(Offset(660f, 40f), Size(150f, 110f)),
                    androidx.compose.ui.geometry.CornerRadius(14f, 14f),
                )
            )
        },
        PARK,
    )

    // Minor street grid, slightly skewed so it does not read as graph paper.
    for (i in 0 until 14) {
        drawLine(N.Neutral900, Offset(i * 70f - 40f, -20f), Offset(i * 70f + 20f, 540f), 6f, StrokeCap.Round)
    }
    for (i in 0 until 9) {
        drawLine(N.Neutral900, Offset(-20f, i * 64f + 10f), Offset(920f, i * 64f - 20f), 6f, StrokeCap.Round)
    }

    // Arterials.
    drawPath(
        Path().apply {
            moveTo(-20f, 360f)
            cubicTo(200f, 330f, 420f, 300f, 920f, 180f)
        },
        ROAD_MAJOR, style = Stroke(width = 14f, cap = StrokeCap.Round),
    )
    drawPath(
        Path().apply {
            moveTo(300f, -20f)
            cubicTo(320f, 160f, 380f, 320f, 460f, 540f)
        },
        ROAD_MAJOR, style = Stroke(width = 12f, cap = StrokeCap.Round),
    )
    drawLine(ROAD_MINOR, Offset(620f, -20f), Offset(560f, 540f), 10f, StrokeCap.Round)

    // The active route. The design puts a Gaussian glow on it; two passes of the
    // accent approximate that without a shader.
    val route = Path().apply {
        moveTo(180f, 500f)
        cubicTo(230f, 420f, 300f, 380f, 352f, 340f)
        cubicTo(420f, 320f, 470f, 300f, 600f, 255f)
        lineTo(610f, 150f)
        cubicTo(640f, 120f, 700f, 100f, 760f, 90f)
    }
    drawPath(route, N.Accent.copy(alpha = 0.22f), style = Stroke(width = 22f, cap = StrokeCap.Round))
    drawPath(route, N.Accent.copy(alpha = 0.35f), style = Stroke(width = 14f, cap = StrokeCap.Round))
    drawPath(route, N.Accent, style = Stroke(width = 8f, cap = StrokeCap.Round))

    // Destination pin.
    drawCircle(N.Bg, radius = 12f, center = Offset(760f, 90f))
    drawCircle(N.Accent, radius = 12f, center = Offset(760f, 90f), style = Stroke(width = 4f))

    // Own position: halo plus heading arrow.
    drawCircle(N.Accent.copy(alpha = 0.18f), radius = 26f, center = Offset(240f, 430f))
    rotate(35f, pivot = Offset(240f, 430f)) {
        drawPath(
            Path().apply {
                moveTo(240f, 412f)
                lineTo(254f, 444f)
                lineTo(240f, 436f)
                lineTo(226f, 444f)
                close()
            },
            N.Accent200,
        )
    }
}
