package com.example.myhondafit.nocturne

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** `.btn-primary` / `.btn-secondary` / `.btn-ghost` from the Nocturne stylesheet. */
enum class BtnKind { Primary, Secondary, Ghost }

/**
 * Nocturne's elevation is a hairline ring rather than a drop shadow
 * (`--shadow-sm: 0 0 0 1px #3f424d`), so "elevated" surfaces are a fill plus a
 * 1dp border.
 */
@Composable
fun NSurface(
    modifier: Modifier = Modifier,
    color: Color = N.Surface,
    ring: Color? = N.RingSm,
    radius: Dp = N.RadiusLg,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier
            .clip(shape)
            .background(color)
            .then(if (ring != null) Modifier.border(1.dp, ring, shape) else Modifier)
    ) { content() }
}

@Composable
fun Btn(
    onClick: () -> Unit,
    kind: BtnKind = BtnKind.Ghost,
    modifier: Modifier = Modifier,
    radius: Dp = N.RadiusMd,
    contentColor: Color? = null,
    background: Color? = null,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val fg = contentColor ?: when (kind) {
        BtnKind.Primary -> N.Accent
        BtnKind.Secondary -> N.Text
        BtnKind.Ghost -> N.Accent
    }
    val border = when (kind) {
        BtnKind.Primary -> BorderStroke(1.dp, N.Accent)
        BtnKind.Secondary -> BorderStroke(1.dp, N.Divider)
        BtnKind.Ghost -> null
    }
    // The stylesheet's :active states: accent at 22% for primary/ghost, text at
    // 14% for secondary. Touch gets the active tint directly — there is no hover
    // on a head unit.
    val press = when (kind) {
        BtnKind.Secondary -> N.Text.copy(alpha = 0.14f)
        else -> N.Accent.copy(alpha = 0.22f)
    }
    val fill = when {
        pressed -> press
        background != null -> background
        else -> Color.Transparent
    }

    Row(
        modifier
            .clip(shape)
            .background(fill)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            ),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalContentColor provides if (enabled) fg else fg.copy(alpha = 0.45f)
        ) { content() }
    }
}

val LocalContentColor = androidx.compose.runtime.compositionLocalOf { N.Text }

/** `.tag.tag-accent` */
@Composable
fun Tag(text: String, modifier: Modifier = Modifier, content: @Composable (RowScope.() -> Unit)? = null) {
    Row(
        modifier
            .clip(RoundedCornerShape(N.RadiusMd * 0.75f))
            .background(N.Accent800)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        content?.invoke(this)
        NText(text, 11.sp, N.Accent100, letterSpacing = 0.02f)
    }
}

/**
 * A Nocturne rule: a hairline that fades to transparent over 48dp at each end
 * instead of stopping cleanly. Used under the status bar and beside the rail.
 */
@Composable
fun FadingRule(modifier: Modifier = Modifier, vertical: Boolean = false) {
    Box(
        modifier.background(
            if (vertical) {
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.12f to N.Divider,
                    0.88f to N.Divider,
                    1f to Color.Transparent,
                )
            } else {
                Brush.horizontalGradient(
                    0f to Color.Transparent,
                    0.06f to N.Divider,
                    0.94f to N.Divider,
                    1f to Color.Transparent,
                )
            }
        )
    )
}

/** Progress track + accent fill. */
@Composable
fun ProgressTrack(fraction: Float, height: Dp, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(height / 2)
    Box(
        modifier
            .height(height)
            .clip(shape)
            .background(N.Neutral800)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .clip(shape)
                .background(N.Accent)
        )
    }
}

@Composable
fun NText(
    text: String,
    size: androidx.compose.ui.unit.TextUnit,
    color: Color = N.Text,
    weight: FontWeight = FontWeight.Normal,
    letterSpacing: Float = 0f,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: androidx.compose.ui.text.style.TextOverflow = androidx.compose.ui.text.style.TextOverflow.Clip,
    lineHeight: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
) {
    androidx.compose.foundation.text.BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontFamily = Inter,
            fontSize = size,
            fontWeight = weight,
            color = color,
            letterSpacing = (size.value * letterSpacing).sp,
            lineHeight = lineHeight,
        ),
        maxLines = maxLines,
        overflow = overflow,
    )
}
