package com.example.myhondafit.nocturne

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

/**
 * The design calls for Phosphor icons. Rather than substitute Material icons —
 * which carry exactly the Android look `PROJECT_GOAL.md` rejects — these are drawn
 * in Phosphor's own 256-unit box at its regular stroke weight (16/256).
 */
enum class Ph {
    House, MapTrifold, MusicNotes, Bluetooth, Plus, Minus, X,
    CellSignalHigh, SpeakerHigh, SpeakerSimpleSlash, BatteryHigh,
    ArrowBendUpRight, ArrowUp, NavigationArrow, MagnifyingGlass, Microphone,
    VinylRecord, SkipBack, SkipForward, Play, Pause, Shuffle, Repeat, Waveform,
    MusicNote, Usb, Radio,
    Phone, PhoneIncoming, PhoneOutgoing, PhoneX, ChatText, AddressBook,
    DeviceMobile, DeviceMobileSpeaker, Headphones, CircleNotch,
}

@Composable
fun PhIcon(icon: Ph, size: Dp, tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) { drawPh(icon, tint) }
}

/** Path builder in 256-unit coordinates, mirroring SVG's `M`/`L`/`C`/`A`. */
private class PB(val path: Path, val u: Float) {
    fun m(x: Float, y: Float) = path.moveTo(x * u, y * u)
    fun l(x: Float, y: Float) = path.lineTo(x * u, y * u)
    fun c(x1: Float, y1: Float, x2: Float, y2: Float, x: Float, y: Float) =
        path.cubicTo(x1 * u, y1 * u, x2 * u, y2 * u, x * u, y * u)

    fun arc(cx: Float, cy: Float, r: Float, startDeg: Float, sweepDeg: Float) =
        path.arcTo(
            Rect(Offset((cx - r) * u, (cy - r) * u), Size(2 * r * u, 2 * r * u)),
            startDeg, sweepDeg, false,
        )

    fun close() = path.close()
}

/** 256-unit Phosphor box mapped onto the canvas. */
private class P(val ds: DrawScope, val color: Color) {
    val u = ds.size.minDimension / 256f
    val sw = 16f * u
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)

    fun o(x: Float, y: Float) = Offset(x * u, y * u)

    fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
        ds.drawLine(color, o(x1, y1), o(x2, y2), sw, StrokeCap.Round)

    fun circle(cx: Float, cy: Float, r: Float, fill: Boolean = false) =
        ds.drawCircle(color, r * u, o(cx, cy), style = if (fill) Fill else stroke)

    fun rect(x: Float, y: Float, w: Float, h: Float, r: Float = 0f, fill: Boolean = true) {
        val p = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    Rect(o(x, y), Size(w * u, h * u)),
                    androidx.compose.ui.geometry.CornerRadius(r * u, r * u),
                )
            )
        }
        ds.drawPath(p, color, style = if (fill) Fill else stroke)
    }

    fun path(fill: Boolean = false, build: PB.() -> Unit) {
        val p = Path()
        PB(p, u).build()
        ds.drawPath(p, color, style = if (fill) Fill else stroke)
    }

    fun arcStroke(cx: Float, cy: Float, r: Float, startDeg: Float, sweepDeg: Float) =
        ds.drawArc(
            color, startDeg, sweepDeg, false,
            topLeft = o(cx - r, cy - r),
            size = Size(2 * r * u, 2 * r * u),
            style = stroke,
        )
}

private fun DrawScope.drawPh(icon: Ph, color: Color) {
    val p = P(this, color)
    when (icon) {
        Ph.House -> {
            p.path { m(32f, 116f); l(128f, 36f); l(224f, 116f) }
            p.path { m(56f, 100f); l(56f, 208f); l(104f, 208f); l(104f, 148f); l(152f, 148f); l(152f, 208f); l(200f, 208f); l(200f, 100f) }
        }
        Ph.MapTrifold -> {
            p.path { m(32f, 72f); l(96f, 48f); l(160f, 72f); l(224f, 48f); l(224f, 184f); l(160f, 208f); l(96f, 184f); l(32f, 208f); close() }
            p.line(96f, 48f, 96f, 184f)
            p.line(160f, 72f, 160f, 208f)
        }
        Ph.MusicNotes -> {
            p.circle(68f, 188f, 28f)
            p.circle(188f, 164f, 28f)
            p.line(96f, 188f, 96f, 76f)
            p.line(216f, 164f, 216f, 52f)
            p.path { m(96f, 76f); l(216f, 52f) }
        }
        Ph.Bluetooth -> p.path { m(80f, 80f); l(176f, 176f); l(128f, 224f); l(128f, 32f); l(176f, 80f); l(80f, 176f) }
        Ph.Plus -> { p.line(128f, 56f, 128f, 200f); p.line(56f, 128f, 200f, 128f) }
        Ph.Minus -> p.line(56f, 128f, 200f, 128f)
        Ph.X -> { p.line(64f, 64f, 192f, 192f); p.line(192f, 64f, 64f, 192f) }

        Ph.CellSignalHigh -> {
            p.rect(44f, 168f, 22f, 44f, 7f)
            p.rect(84f, 136f, 22f, 76f, 7f)
            p.rect(124f, 104f, 22f, 108f, 7f)
            p.rect(164f, 60f, 22f, 152f, 7f)
        }
        Ph.SpeakerHigh -> {
            p.path(fill = true) { m(32f, 96f); l(76f, 96f); l(136f, 44f); l(136f, 212f); l(76f, 160f); l(32f, 160f); close() }
            p.arcStroke(150f, 128f, 36f, -55f, 110f)
            p.arcStroke(150f, 128f, 64f, -55f, 110f)
        }
        Ph.SpeakerSimpleSlash -> {
            p.path(fill = true) { m(32f, 96f); l(76f, 96f); l(136f, 44f); l(136f, 212f); l(76f, 160f); l(32f, 160f); close() }
            p.line(168f, 96f, 224f, 160f)
            p.line(224f, 96f, 168f, 160f)
        }
        Ph.BatteryHigh -> {
            p.rect(16f, 76f, 184f, 104f, 16f, fill = false)
            p.rect(212f, 108f, 16f, 40f, 8f)
            p.rect(36f, 96f, 144f, 64f, 8f)
        }

        Ph.ArrowBendUpRight -> {
            p.path { m(56f, 208f); l(56f, 120f); arc(104f, 120f, 48f, 180f, 90f); l(200f, 72f) }
            p.path { m(160f, 32f); l(200f, 72f); l(160f, 112f) }
        }
        Ph.ArrowUp -> { p.line(128f, 208f, 128f, 56f); p.path { m(72f, 112f); l(128f, 56f); l(184f, 112f) } }
        Ph.NavigationArrow -> p.path { m(128f, 28f); l(212f, 212f); l(128f, 170f); l(44f, 212f); close() }
        Ph.MagnifyingGlass -> { p.circle(112f, 112f, 64f); p.line(158f, 158f, 216f, 216f) }
        Ph.Microphone -> {
            p.rect(96f, 28f, 64f, 116f, 32f, fill = false)
            p.arcStroke(128f, 128f, 64f, 0f, 180f)
            p.line(128f, 192f, 128f, 228f)
        }

        Ph.VinylRecord -> { p.circle(128f, 128f, 96f); p.circle(128f, 128f, 44f); p.circle(128f, 128f, 14f, fill = true) }
        Ph.SkipBack -> {
            p.rect(44f, 56f, 18f, 144f, 6f)
            p.path(fill = true) { m(212f, 56f); l(212f, 200f); l(84f, 128f); close() }
        }
        Ph.SkipForward -> {
            p.rect(194f, 56f, 18f, 144f, 6f)
            p.path(fill = true) { m(44f, 56f); l(44f, 200f); l(172f, 128f); close() }
        }
        Ph.Play -> p.path(fill = true) { m(72f, 44f); l(72f, 212f); l(212f, 128f); close() }
        Ph.Pause -> { p.rect(72f, 48f, 34f, 160f, 8f); p.rect(150f, 48f, 34f, 160f, 8f) }
        Ph.Shuffle -> {
            p.path { m(28f, 72f); l(72f, 72f); c(120f, 72f, 136f, 184f, 184f, 184f); l(216f, 184f) }
            p.path { m(28f, 184f); l(72f, 184f); c(120f, 184f, 136f, 72f, 184f, 72f); l(216f, 72f) }
            p.path { m(184f, 40f); l(216f, 72f); l(184f, 104f) }
            p.path { m(184f, 152f); l(216f, 184f); l(184f, 216f) }
        }
        Ph.Repeat -> {
            p.path { m(40f, 116f); l(40f, 92f); arc(64f, 92f, 24f, 180f, 90f); l(216f, 68f) }
            p.path { m(184f, 36f); l(216f, 68f); l(184f, 100f) }
            p.path { m(216f, 140f); l(216f, 164f); arc(192f, 164f, 24f, 0f, 90f); l(40f, 188f) }
            p.path { m(72f, 156f); l(40f, 188f); l(72f, 220f) }
        }
        Ph.Waveform -> {
            p.line(36f, 108f, 36f, 148f)
            p.line(72f, 76f, 72f, 180f)
            p.line(108f, 40f, 108f, 216f)
            p.line(144f, 72f, 144f, 184f)
            p.line(180f, 96f, 180f, 160f)
            p.line(216f, 112f, 216f, 144f)
        }
        Ph.MusicNote -> {
            p.circle(84f, 184f, 32f)
            p.line(116f, 184f, 116f, 60f)
            p.path { m(116f, 60f); l(204f, 36f); l(204f, 72f); l(116f, 96f) }
        }
        Ph.Usb -> {
            p.line(128f, 220f, 128f, 60f)
            p.circle(128f, 44f, 14f, fill = true)
            p.line(128f, 160f, 84f, 124f)
            p.rect(60f, 100f, 36f, 26f, 6f)
            p.line(128f, 120f, 172f, 88f)
            p.path(fill = true) { m(196f, 72f); l(196f, 104f); l(166f, 88f); close() }
        }
        Ph.Radio -> {
            p.rect(24f, 84f, 208f, 132f, 16f, fill = false)
            p.circle(80f, 150f, 30f)
            p.line(160f, 124f, 208f, 124f)
            p.line(160f, 164f, 208f, 164f)
            p.line(168f, 76f, 216f, 36f)
        }

        Ph.Phone -> p.phone()
        Ph.PhoneIncoming -> { p.phone(); p.path { m(160f, 20f); l(160f, 76f); l(216f, 76f) }; p.line(160f, 76f, 224f, 12f) }
        Ph.PhoneOutgoing -> { p.phone(); p.path { m(168f, 12f); l(224f, 12f); l(224f, 68f) }; p.line(224f, 12f, 160f, 76f) }
        Ph.PhoneX -> { p.phone(); p.line(168f, 20f, 224f, 76f); p.line(224f, 20f, 168f, 76f) }
        Ph.ChatText -> {
            p.path { m(48f, 40f); l(208f, 40f); arc(208f, 56f, 16f, 270f, 90f); l(224f, 160f); arc(208f, 160f, 16f, 0f, 90f); l(96f, 176f); l(48f, 216f); l(48f, 176f); arc(48f, 160f, 16f, 90f, 90f); l(32f, 56f); arc(48f, 56f, 16f, 180f, 90f); close() }
            p.line(76f, 88f, 180f, 88f)
            p.line(76f, 128f, 148f, 128f)
        }
        Ph.AddressBook -> {
            p.rect(64f, 28f, 160f, 200f, 16f, fill = false)
            p.line(32f, 72f, 64f, 72f)
            p.line(32f, 128f, 64f, 128f)
            p.line(32f, 184f, 64f, 184f)
            p.circle(144f, 112f, 26f)
            p.arcStroke(144f, 186f, 44f, 180f, 180f)
        }
        Ph.DeviceMobile -> {
            p.rect(72f, 24f, 112f, 208f, 16f, fill = false)
            p.line(108f, 200f, 148f, 200f)
        }
        Ph.DeviceMobileSpeaker -> {
            p.rect(72f, 24f, 112f, 208f, 16f, fill = false)
            p.line(108f, 56f, 148f, 56f)
            p.line(108f, 200f, 148f, 200f)
        }
        Ph.Headphones -> {
            p.path { m(40f, 168f); l(40f, 132f); arc(128f, 132f, 88f, 180f, 180f); l(216f, 168f) }
            p.rect(20f, 152f, 44f, 72f, 16f, fill = false)
            p.rect(192f, 152f, 44f, 72f, 16f, fill = false)
        }
        Ph.CircleNotch -> p.arcStroke(128f, 128f, 92f, 40f, 280f)
    }
}

/** Shared handset outline — the `-incoming`/`-outgoing`/`-x` variants decorate it. */
private fun P.phone() = path {
    m(96f, 44f)
    c(72f, 44f, 44f, 60f, 44f, 84f)
    c(44f, 160f, 104f, 220f, 180f, 220f)
    c(204f, 220f, 220f, 192f, 220f, 168f)
    l(172f, 148f)
    l(148f, 180f)
    c(120f, 164f, 100f, 144f, 84f, 116f)
    l(116f, 92f)
    close()
}
