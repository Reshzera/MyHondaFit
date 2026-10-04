@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.example.myhondafit.nocturne

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myhondafit.R

/**
 * Nocturne design-system tokens, transcribed from the Claude Design project
 * `6bf1b4bf-f345-446c-bb25-f52650bc6da5` ("Honda Fit Multimidia"), file
 * `_ds/nocturne-4cbc6581-…/styles.css`. That stylesheet is the source of truth
 * for the look; this is its Kotlin mirror. Retune there, then update here.
 */
object N {
    val Bg = Color(0xFF161826)
    val Surface = Color(0xFF232532)
    val Text = Color(0xFFE9E9ED)
    val Accent = Color(0xFF9184D9)

    /** `--color-divider: color-mix(in srgb, #e9e9ed 16%, transparent)` */
    val Divider = Color(0xFFE9E9ED).copy(alpha = 0.16f)

    val Neutral100 = Color(0xFFF3F5FE)
    val Neutral200 = Color(0xFFE4E7F5)
    val Neutral300 = Color(0xFFCFD3E5)
    val Neutral400 = Color(0xFFB2B6CA)
    val Neutral500 = Color(0xFF9397AB)
    val Neutral600 = Color(0xFF75798C)
    val Neutral700 = Color(0xFF595D6C)
    val Neutral800 = Color(0xFF3F424D)
    val Neutral900 = Color(0xFF292B31)

    val Accent100 = Color(0xFFF5F4FF)
    val Accent200 = Color(0xFFE7E5FE)
    val Accent300 = Color(0xFFD2CEFD)
    val Accent400 = Color(0xFFB5ABFC)
    val Accent500 = Color(0xFF968AE0)
    val Accent600 = Color(0xFF796CBF)
    val Accent700 = Color(0xFF5D5294)
    val Accent800 = Color(0xFF423A6A)
    val Accent900 = Color(0xFF2B2741)

    val RadiusSm = 4.dp
    val RadiusMd = 8.dp
    val RadiusLg = 14.dp

    /**
     * Elevation in Nocturne is a hairline ring, not a drop shadow:
     * `--shadow-sm: 0 0 0 1px #3f424d`, `--shadow-md: 0 0 0 1px #595d6c, …`.
     * Rendered here as a 1dp border so the ring reads the same way it does in CSS.
     */
    val RingSm = Neutral800
    val RingMd = Neutral700
}

/**
 * The design system asks for Inter. Bundled as a variable font, so the 400/500
 * instances come from the weight axis rather than from synthetic bolding.
 */
val Inter = FontFamily(
    Font(
        R.font.inter,
        FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        R.font.inter,
        FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
        R.font.inter,
        FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600)),
    ),
)
