package com.example.myhondafit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhondafit.nocturne.Btn
import com.example.myhondafit.nocturne.BtnKind
import com.example.myhondafit.nocturne.N
import com.example.myhondafit.nocturne.NSurface
import com.example.myhondafit.nocturne.NText
import com.example.myhondafit.nocturne.Ph
import com.example.myhondafit.nocturne.PhIcon
import com.example.myhondafit.nocturne.ProgressTrack
import com.example.myhondafit.shell.HeadUnitState
import com.example.myhondafit.shell.Source

@Composable
fun MusicScreen(state: HeadUnitState) {
    Row(
        Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        NowPlaying(state, Modifier.weight(1f).fillMaxHeight())
        QueuePane(state, Modifier.width(400.dp).fillMaxHeight())
    }
}

@Composable
private fun NowPlaying(state: HeadUnitState, modifier: Modifier) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(N.RadiusLg))
                .background(artBrush(state.track.hue)),
            contentAlignment = Alignment.Center,
        ) {
            PhIcon(Ph.VinylRecord, 110.dp, N.Accent300.copy(alpha = 0.6f))
        }

        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText(
                    state.sourceLabel.uppercase(),
                    13.sp,
                    N.Accent300,
                    letterSpacing = 0.08f,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                NText(
                    state.track.title,
                    36.sp,
                    weight = FontWeight.Medium,
                    letterSpacing = -0.015f,
                    lineHeight = 40.sp,
                )
                NText("${state.track.artist} · ${state.track.album}", 19.sp, N.Neutral300)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgressTrack(state.progress, 6.dp, Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    NText(state.elapsedText, 14.sp, N.Neutral300)
                    NText(state.track.len, 14.sp, N.Neutral300)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Btn(state::toggleShuffle, BtnKind.Ghost, Modifier.size(56.dp)) {
                    PhIcon(Ph.Shuffle, 24.dp, if (state.shuffle) N.Accent else N.Neutral300)
                }
                Btn(state::previous, BtnKind.Ghost, Modifier.size(68.dp)) {
                    PhIcon(Ph.SkipBack, 32.dp, N.Accent)
                }
                Btn(
                    state::togglePlay,
                    BtnKind.Primary,
                    Modifier.size(88.dp),
                    radius = 44.dp,
                ) {
                    PhIcon(if (state.playing) Ph.Pause else Ph.Play, 38.dp, N.Accent)
                }
                Btn(state::next, BtnKind.Ghost, Modifier.size(68.dp)) {
                    PhIcon(Ph.SkipForward, 32.dp, N.Accent)
                }
                Btn({}, BtnKind.Ghost, Modifier.size(56.dp)) {
                    PhIcon(Ph.Repeat, 24.dp, N.Accent)
                }
            }
        }
    }
}

@Composable
private fun QueuePane(state: HeadUnitState, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NSurface(Modifier.fillMaxWidth(), radius = N.RadiusMd) {
            Row(
                Modifier.padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Source.entries.forEach { src ->
                    val active = state.source == src
                    Btn(
                        onClick = { state.source = src },
                        kind = BtnKind.Ghost,
                        radius = 6.dp,
                        background = if (active) N.Accent900 else Color.Transparent,
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) {
                        PhIcon(src.icon, 20.dp, if (active) N.Accent200 else N.Neutral300)
                        NText(
                            src.label,
                            15.sp,
                            if (active) N.Accent200 else N.Neutral300,
                            weight = FontWeight.Medium,
                        )
                    }
                }
            }
        }

        NText(
            "A SEGUIR",
            13.sp,
            N.Neutral300,
            letterSpacing = 0.08f,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp),
        )

        LazyColumn(
            Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(state.tracks) { index, track ->
                val current = index == state.trackIndex
                Btn(
                    onClick = { state.play(index) },
                    kind = BtnKind.Ghost,
                    radius = N.RadiusMd,
                    background = if (current) N.Accent900 else Color.Transparent,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 8.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(artBrush(track.hue)),
                            contentAlignment = Alignment.Center,
                        ) {
                            val icon = when {
                                current && state.playing -> Ph.Waveform
                                current -> Ph.Pause
                                else -> Ph.MusicNote
                            }
                            PhIcon(icon, 20.dp, N.Accent300)
                        }
                        Column(Modifier.weight(1f)) {
                            NText(
                                track.title,
                                16.sp,
                                if (current) N.Accent200 else N.Text,
                                weight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            NText(track.artist, 14.sp, N.Neutral300, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        NText(track.len, 14.sp, N.Neutral300)
                    }
                }
            }
        }
    }
}
