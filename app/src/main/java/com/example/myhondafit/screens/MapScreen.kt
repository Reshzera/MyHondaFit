package com.example.myhondafit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhondafit.nocturne.Btn
import com.example.myhondafit.nocturne.BtnKind
import com.example.myhondafit.nocturne.FadingRule
import com.example.myhondafit.nocturne.N
import com.example.myhondafit.nocturne.NSurface
import com.example.myhondafit.nocturne.NText
import com.example.myhondafit.nocturne.Ph
import com.example.myhondafit.nocturne.PhIcon
import com.example.myhondafit.shell.HeadUnitState
import com.example.myhondafit.shell.Screen

@Composable
fun MapScreen(state: HeadUnitState) {
    Box(Modifier.fillMaxSize().background(N.Surface)) {
        MapCanvas(Modifier.fillMaxSize())

        // Search + next manoeuvre, top left.
        Column(
            Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 22.dp)
                .width(400.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Glass(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PhIcon(Ph.MagnifyingGlass, 22.dp, N.Neutral300)
                    NText("Para onde?", 17.sp, N.Neutral300)
                    Spacer(Modifier.weight(1f))
                    PhIcon(Ph.Microphone, 22.dp, N.Accent)
                }
            }

            Glass(Modifier.fillMaxWidth(), ring = N.RingMd) {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        PhIcon(Ph.ArrowBendUpRight, 52.dp, N.Accent)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            NText("350 m", 34.sp, weight = FontWeight.Medium)
                            NText("Av. Brigadeiro Faria Lima", 17.sp, N.Neutral300)
                        }
                    }
                    FadingRule(Modifier.fillMaxWidth().height(1.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        PhIcon(Ph.ArrowUp, 20.dp, N.Neutral300)
                        NText("Depois, siga 1,2 km pela Av. Rebouças", 15.sp, N.Neutral300)
                    }
                }
            }
        }

        // Map controls, top right.
        Column(
            Modifier.align(Alignment.TopEnd).padding(top = 22.dp, end = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MapControl(Ph.Plus)
            MapControl(Ph.Minus)
            MapControl(Ph.NavigationArrow)
        }

        // Trip summary, bottom.
        Glass(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 24.dp, bottom = 22.dp)
                .fillMaxWidth(),
            ring = N.RingMd,
        ) {
            Row(
                Modifier.padding(start = 24.dp, top = 14.dp, end = 14.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                TripStat("Chegada", "18:42")
                TripStat("Tempo", "12 min")
                TripStat("Distância", "6,4 km")
                Column(Modifier.weight(1f)) {
                    NText("Destino", 13.sp, N.Neutral300)
                    NText(
                        "Shopping Eldorado · Av. Rebouças, 3970",
                        17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Btn({}, BtnKind.Ghost, Modifier.height(56.dp)) {
                    Row(
                        Modifier.padding(horizontal = 22.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PhIcon(Ph.SpeakerSimpleSlash, 20.dp, N.Accent)
                        NText("Silenciar", 16.sp, N.Accent, weight = FontWeight.Medium)
                    }
                }
                Btn({ state.go(Screen.Home) }, BtnKind.Primary, Modifier.height(56.dp)) {
                    Row(
                        Modifier.padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PhIcon(Ph.X, 20.dp, N.Accent)
                        NText("Encerrar rota", 16.sp, N.Accent, weight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun TripStat(label: String, value: String) {
    Column {
        NText(label, 13.sp, N.Neutral300)
        NText(value, 26.sp, weight = FontWeight.Medium)
    }
}

@Composable
private fun MapControl(icon: Ph) {
    Btn(
        onClick = {},
        kind = BtnKind.Secondary,
        modifier = Modifier.size(60.dp),
        background = N.Bg.copy(alpha = 0.90f),
    ) { PhIcon(icon, 26.dp, N.Text) }
}

@Composable
private fun Glass(
    modifier: Modifier = Modifier,
    ring: androidx.compose.ui.graphics.Color = N.RingSm,
    content: @Composable () -> Unit,
) {
    NSurface(
        modifier = modifier,
        color = N.Bg.copy(alpha = 0.90f),
        ring = ring,
        radius = N.RadiusMd,
    ) { content() }
}
