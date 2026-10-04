package com.example.myhondafit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.myhondafit.nocturne.Tag
import com.example.myhondafit.nocturne.artColors
import com.example.myhondafit.shell.HeadUnitState
import com.example.myhondafit.shell.Screen

fun artBrush(hue: Float) = Brush.linearGradient(artColors(hue))

@Composable
fun HomeScreen(state: HeadUnitState) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(start = 28.dp, top = 24.dp, end = 28.dp, bottom = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        NavigationCard(state, Modifier.weight(1.35f).fillMaxHeight())

        Column(
            Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            NowPlayingCard(state, Modifier.fillMaxWidth().weight(1f))
            PhoneCard(state, Modifier.fillMaxWidth().weight(1f))
        }
    }
}

@Composable
private fun NavigationCard(state: HeadUnitState, modifier: Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(N.RadiusLg))
            .background(N.Surface)
            .clickable { state.go(Screen.Map) }
    ) {
        MapCanvas(Modifier.fillMaxSize())

        Glass(
            Modifier
                .align(Alignment.TopStart)
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Row(
                Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                PhIcon(Ph.ArrowBendUpRight, 40.dp, N.Accent)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    NText("350 m", 28.sp, weight = FontWeight.Medium)
                    NText("Vire à direita na Av. Brigadeiro Faria Lima", 16.sp, N.Neutral300)
                }
            }
        }

        Glass(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
            Row(
                Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Stat("Chegada", "18:42")
                Stat("Restante", "12 min · 6,4 km")
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String, valueSize: Int = 22) {
    Column {
        NText(label, 13.sp, N.Neutral300)
        NText(value, valueSize.sp, weight = FontWeight.Medium)
    }
}

/** The design's `color-mix(bg 88%, transparent)` panels floating over the map. */
@Composable
private fun Glass(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    NSurface(
        modifier = modifier,
        color = N.Bg.copy(alpha = 0.88f),
        radius = N.RadiusMd,
    ) { content() }
}

@Composable
private fun NowPlayingCard(state: HeadUnitState, modifier: Modifier) {
    NSurface(modifier) {
        Row(
            Modifier.fillMaxSize().padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(132.dp)
                    .clip(RoundedCornerShape(N.RadiusMd))
                    .background(artBrush(state.track.hue)),
                contentAlignment = Alignment.Center,
            ) {
                PhIcon(Ph.VinylRecord, 54.dp, N.Accent300.copy(alpha = 0.7f))
            }

            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    NText(
                        state.sourceLabel.uppercase(),
                        12.sp,
                        N.Accent300,
                        letterSpacing = 0.08f,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    NText(
                        state.track.title,
                        22.sp,
                        weight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    NText(state.track.artist, 15.sp, N.Neutral300, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                ProgressTrack(state.progress, 4.dp, Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Btn(state::previous, BtnKind.Ghost, Modifier.width(56.dp).height(52.dp)) {
                        PhIcon(Ph.SkipBack, 26.dp, N.Accent)
                    }
                    Btn(state::togglePlay, BtnKind.Primary, Modifier.width(68.dp).height(52.dp)) {
                        PhIcon(if (state.playing) Ph.Pause else Ph.Play, 28.dp, N.Accent)
                    }
                    Btn(state::next, BtnKind.Ghost, Modifier.width(56.dp).height(52.dp)) {
                        PhIcon(Ph.SkipForward, 26.dp, N.Accent)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhoneCard(state: HeadUnitState, modifier: Modifier) {
    NSurface(modifier.clickable { state.go(Screen.Phone) }) {
        Column(
            Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PhIcon(Ph.Bluetooth, 22.dp, N.Accent)
                    NText("Bluetooth", 15.sp, N.Neutral300)
                }
                Tag(state.btStatus)
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                NText(state.connectedName, 24.sp, weight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                NText(state.phoneMeta, 15.sp, N.Neutral300)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Btn({ state.go(Screen.Phone) }, BtnKind.Secondary, Modifier.height(52.dp)) {
                    Row(
                        Modifier.padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PhIcon(Ph.Phone, 20.dp, N.Text)
                        NText("Ligar", 15.sp, N.Text, weight = FontWeight.Medium)
                    }
                }
                Btn({ state.go(Screen.Phone) }, BtnKind.Ghost, Modifier.height(52.dp)) {
                    Row(
                        Modifier.padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PhIcon(Ph.ChatText, 20.dp, N.Accent)
                        NText("Mensagens", 15.sp, N.Accent, weight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
