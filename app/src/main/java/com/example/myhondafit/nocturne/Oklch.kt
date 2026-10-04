package com.example.myhondafit.nocturne

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * The design generates album art as `oklch(0.42 0.09 H)` → `oklch(0.2 0.04 H+30)`.
 * Android has no OKLCH literal, so we convert: OKLCH → OKLab → LMS → linear sRGB
 * → sRGB. Reproducing the math keeps the artwork hues exactly as designed instead
 * of eyeballing replacements.
 */
fun oklch(l: Float, c: Float, hDegrees: Float): Color {
    val h = hDegrees * (Math.PI.toFloat() / 180f)
    val a = c * cos(h)
    val b = c * sin(h)

    val lp = l + 0.3963377774f * a + 0.2158037573f * b
    val mp = l - 0.1055613458f * a - 0.0638541728f * b
    val sp = l - 0.0894841775f * a - 1.2914855480f * b

    val lc = lp * lp * lp
    val mc = mp * mp * mp
    val sc = sp * sp * sp

    val r = 4.0767416621f * lc - 3.3077115913f * mc + 0.2309699292f * sc
    val g = -1.2684380046f * lc + 2.6097574011f * mc - 0.3413193965f * sc
    val bl = -0.0041960863f * lc - 0.7034186147f * mc + 1.7076147010f * sc

    return Color(gamma(r), gamma(g), gamma(bl), 1f)
}

private fun gamma(linear: Float): Float {
    val v = if (linear <= 0.0031308f) 12.92f * linear
    else 1.055f * linear.toDouble().pow(1.0 / 2.4).toFloat() - 0.055f
    return v.coerceIn(0f, 1f)
}

/** `linear-gradient(140deg, oklch(0.42 0.09 h), oklch(0.2 0.04 h+30))` */
fun artColors(hue: Float): List<Color> =
    listOf(oklch(0.42f, 0.09f, hue), oklch(0.20f, 0.04f, hue + 30f))
