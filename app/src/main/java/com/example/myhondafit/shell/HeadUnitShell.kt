package com.example.myhondafit.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.myhondafit.nocturne.Btn
import com.example.myhondafit.nocturne.BtnKind
import com.example.myhondafit.nocturne.FadingRule
import com.example.myhondafit.nocturne.N
import com.example.myhondafit.nocturne.NText
import com.example.myhondafit.nocturne.Ph
import com.example.myhondafit.nocturne.PhIcon
import com.example.myhondafit.screens.BluetoothScreen
import com.example.myhondafit.screens.HomeScreen
import com.example.myhondafit.screens.MapScreen
import com.example.myhondafit.screens.MusicScreen
import kotlinx.coroutines.delay

/**
 * The whole user-facing surface: our own status bar, our own rail, our own
 * screens. No launcher grid, no app drawer, no system chrome — see
 * `PROJECT_GOAL.md`.
 */
@Composable
fun HeadUnitShell(state: HeadUnitState = remember { HeadUnitState() }) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000)
            state.tick()
        }
    }

    Column(Modifier.fillMaxSize().background(N.Bg)) {
        StatusBar(state)
        Row(Modifier.fillMaxSize()) {
            NavRail(state)
            Box(Modifier.fillMaxSize()) {
                when (state.screen) {
                    Screen.Home -> HomeScreen(state)
                    Screen.Map -> MapScreen(state)
                    Screen.Music -> MusicScreen(state)
                    Screen.Phone -> BluetoothScreen(state)
                }
            }
        }
    }
}

@Composable
private fun StatusBar(state: HeadUnitState) {
    Box(Modifier.fillMaxWidth().height(52.dp)) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                NText(state.timeText, 22.sp, weight = FontWeight.Medium, letterSpacing = -0.01f)
                NText("24°C · São Paulo", 15.sp, N.Neutral300)
            }

            Spacer(Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                if (state.connected) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        PhIcon(Ph.Bluetooth, 20.dp, N.Accent300)
                        NText(state.connectedName, 14.sp, N.Accent300)
                    }
                }
                PhIcon(Ph.CellSignalHigh, 20.dp, N.Neutral300)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PhIcon(Ph.SpeakerHigh, 20.dp, N.Neutral300)
                    NText(state.volume.toString(), 15.sp, N.Neutral300)
                }
            }
        }
        FadingRule(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
        )
    }
}

private data class NavItem(val screen: Screen, val icon: Ph, val label: String)

@Composable
private fun NavRail(state: HeadUnitState) {
    val items = listOf(
        NavItem(Screen.Home, Ph.House, "Início"),
        NavItem(Screen.Map, Ph.MapTrifold, "Mapa"),
        NavItem(Screen.Music, Ph.MusicNotes, "Música"),
        NavItem(Screen.Phone, Ph.Bluetooth, "Bluetooth"),
    )

    Box(Modifier.width(104.dp).fillMaxHeight()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items.forEach { item ->
                RailButton(item, active = state.screen == item.screen) { state.go(item.screen) }
            }

            Spacer(Modifier.weight(1f))

            Btn(
                onClick = state::volumeUp,
                kind = BtnKind.Ghost,
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) { PhIcon(Ph.Plus, 24.dp, N.Accent) }

            Btn(
                onClick = state::volumeDown,
                kind = BtnKind.Ghost,
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) { PhIcon(Ph.Minus, 24.dp, N.Accent) }
        }
        FadingRule(
            Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(1.dp),
            vertical = true,
        )
    }
}

@Composable
private fun RailButton(item: NavItem, active: Boolean, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(86.dp)) {
        Btn(
            onClick = onClick,
            kind = BtnKind.Ghost,
            radius = N.RadiusLg,
            background = if (active) N.Accent900 else Color.Transparent,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                PhIcon(item.icon, 30.dp, if (active) N.Accent200 else N.Neutral300)
                NText(
                    item.label,
                    12.sp,
                    if (active) N.Accent200 else N.Neutral300,
                    weight = FontWeight.Medium,
                    letterSpacing = 0.02f,
                )
            }
        }
        // The active marker sits off the button's left edge, flush with the rail.
        if (active) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-10).dp)
                    .width(3.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(N.Accent)
            )
        }
    }
}
