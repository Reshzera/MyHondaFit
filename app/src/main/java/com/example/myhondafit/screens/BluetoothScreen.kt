package com.example.myhondafit.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhondafit.nocturne.Btn
import com.example.myhondafit.nocturne.BtnKind
import com.example.myhondafit.nocturne.N
import com.example.myhondafit.nocturne.NSurface
import com.example.myhondafit.nocturne.NText
import com.example.myhondafit.nocturne.Ph
import com.example.myhondafit.nocturne.PhIcon
import com.example.myhondafit.nocturne.Tag
import com.example.myhondafit.shell.HeadUnitState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BluetoothScreen(state: HeadUnitState) {
    Row(
        Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        DevicesPane(state, Modifier.weight(1f).fillMaxHeight())
        ConnectionPane(state, Modifier.weight(1f).fillMaxHeight())
    }
}

@Composable
private fun DevicesPane(state: HeadUnitState, modifier: Modifier) {
    val scope = rememberCoroutineScope()
    Column(modifier, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                NText("Bluetooth", 30.sp, weight = FontWeight.Medium, letterSpacing = -0.015f)
                NText("Visível como “Honda Fit”", 15.sp, N.Neutral300)
            }
            BtSwitch(state.btOn, state::toggleBluetooth)
        }

        if (state.btOn) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NText(
                    "DISPOSITIVOS PAREADOS",
                    13.sp,
                    N.Neutral300,
                    letterSpacing = 0.08f,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                )

                state.devices.forEachIndexed { index, device ->
                    val connected = state.connectedIndex == index
                    NSurface(
                        Modifier.fillMaxWidth(),
                        ring = if (connected) N.Accent700 else N.RingSm,
                        radius = N.RadiusMd,
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 14.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Box(
                                Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(N.RadiusMd))
                                    .background(if (connected) N.Accent900 else N.Neutral900),
                                contentAlignment = Alignment.Center,
                            ) {
                                PhIcon(device.icon, 26.dp, if (connected) N.Accent else N.Neutral300)
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                NText(device.name, 17.sp, weight = FontWeight.Medium)
                                NText(
                                    if (connected) "Conectado · chamadas e áudio" else "Pareado",
                                    14.sp,
                                    if (connected) N.Accent300 else N.Neutral300,
                                )
                            }
                            Btn(
                                onClick = { state.toggleDevice(index) },
                                kind = if (connected) BtnKind.Ghost else BtnKind.Primary,
                                modifier = Modifier.height(52.dp),
                            ) {
                                NText(
                                    if (connected) "Desconectar" else "Conectar",
                                    15.sp,
                                    N.Accent,
                                    weight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                )
                            }
                        }
                    }
                }

                Btn(
                    onClick = {
                        if (!state.scanning) scope.launch {
                            state.scanning = true
                            delay(3_000)
                            state.scanning = false
                        }
                    },
                    kind = BtnKind.Ghost,
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 6.dp),
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        PhIcon(state.scanIcon, 22.dp, N.Accent)
                        NText(state.scanLabel, 16.sp, N.Accent, weight = FontWeight.Medium)
                    }
                }
            }
        } else {
            NSurface(Modifier.fillMaxWidth(), radius = N.RadiusMd) {
                NText(
                    "Bluetooth desligado. Ligue para conectar seu celular, ouvir música e fazer chamadas pelo sistema.",
                    16.sp,
                    N.Neutral300,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(24.dp),
                )
            }
        }
    }
}

@Composable
private fun BtSwitch(on: Boolean, onToggle: () -> Unit) {
    val knobOffset by animateDpAsState(if (on) 38.dp else 5.dp, label = "knob")
    Box(
        Modifier
            .size(width = 76.dp, height = 44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (on) N.Accent800 else N.Neutral900)
            .border(
                1.dp,
                if (on) N.Accent else N.Neutral700,
                RoundedCornerShape(22.dp),
            )
            .clickable(onClick = onToggle),
    ) {
        Box(
            Modifier
                .offset(x = knobOffset, y = 5.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(if (on) N.Accent200 else N.Neutral400)
        )
    }
}

@Composable
private fun ConnectionPane(state: HeadUnitState, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        if (state.connected) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(N.RadiusLg))
                    .background(
                        Brush.linearGradient(
                            0f to N.Accent900,
                            0.7f to N.Surface,
                        )
                    )
                    .border(1.dp, N.RingSm, RoundedCornerShape(N.RadiusLg))
            ) {
                Column(
                    Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            NText("CONECTADO", 13.sp, N.Accent300, letterSpacing = 0.08f)
                            NText(state.connectedName, 26.sp, weight = FontWeight.Medium)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            PhIcon(Ph.CellSignalHigh, 20.dp, N.Neutral300)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                PhIcon(Ph.BatteryHigh, 22.dp, N.Neutral300)
                                NText("78%", 15.sp, N.Neutral300)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Tag("Chamadas") { PhIcon(Ph.Phone, 13.dp, N.Accent100) }
                        Tag("Áudio") { PhIcon(Ph.MusicNote, 13.dp, N.Accent100) }
                        Tag("Contatos") { PhIcon(Ph.AddressBook, 13.dp, N.Accent100) }
                    }
                }
            }

            NText(
                "CHAMADAS RECENTES",
                13.sp,
                N.Neutral300,
                letterSpacing = 0.08f,
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                state.calls.forEach { call ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(N.RadiusMd))
                            .padding(start = 4.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            Modifier.size(48.dp).clip(CircleShape).background(N.Neutral900),
                            contentAlignment = Alignment.Center,
                        ) {
                            NText(call.initials, 16.sp, N.Accent300, weight = FontWeight.Medium)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            NText(call.name, 16.sp, weight = FontWeight.Medium)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                PhIcon(call.icon, 14.dp, N.Neutral300)
                                NText(call.whenText, 14.sp, N.Neutral300)
                            }
                        }
                        Btn({}, BtnKind.Secondary, Modifier.size(52.dp)) {
                            PhIcon(Ph.Phone, 22.dp, N.Text)
                        }
                    }
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                PhIcon(Ph.DeviceMobileSpeaker, 48.dp, N.Accent)
                Spacer(Modifier.height(10.dp))
                NText("Nenhum celular conectado", 22.sp, weight = FontWeight.Medium)
                Spacer(Modifier.height(10.dp))
                NText(
                    "Escolha um dispositivo pareado ou procure um novo para usar chamadas e áudio.",
                    15.sp,
                    N.Neutral300,
                    lineHeight = 22.sp,
                    modifier = Modifier.widthIn(max = 340.dp),
                )
            }
        }
    }
}
