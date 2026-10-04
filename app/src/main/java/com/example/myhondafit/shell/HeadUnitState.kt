package com.example.myhondafit.shell

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myhondafit.nocturne.Ph
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class Screen { Home, Map, Music, Phone }

enum class Source(val id: String, val label: String, val icon: Ph) {
    Bt("bt", "Bluetooth", Ph.Bluetooth),
    Usb("usb", "USB", Ph.Usb),
    Fm("fm", "Rádio", Ph.Radio),
}

data class Track(
    val title: String,
    val artist: String,
    val album: String,
    val len: String,
    val seconds: Int,
    val hue: Float,
)

data class PairedDevice(val name: String, val icon: Ph)

data class RecentCall(val initials: String, val name: String, val whenText: String, val icon: Ph)

/**
 * Translation of the design's `DCLogic` component state. The same fields, the
 * same derived values, so the UI below reads the way the design template does.
 */
class HeadUnitState {
    var screen by mutableStateOf(Screen.Home)
    var playing by mutableStateOf(true)
    var elapsedSeconds by mutableIntStateOf(74)
    var trackIndex by mutableIntStateOf(0)
    var volume by mutableIntStateOf(18)
    var shuffle by mutableStateOf(false)
    var source by mutableStateOf(Source.Bt)
    var btOn by mutableStateOf(true)
    var scanning by mutableStateOf(false)

    /** Index into [devices], or null when nothing is connected. */
    var connectedIndex by mutableStateOf<Int?>(0)

    var now by mutableStateOf(LocalTime.now())

    val tracks = listOf(
        Track("Luz de Estrada", "Marina Vale", "Rota 116", "3:48", 228, 285f),
        Track("Noite em Pinheiros", "Os Ventania", "Faixa Azul", "4:12", 252, 250f),
        Track("Retrovisor", "Clara Ruiz", "Retrovisor", "3:05", 185, 220f),
        Track("Serra do Mar", "Trio Paralelo", "Litoral", "5:01", 301, 300f),
        Track("Semáforo Verde", "Bento & Lia", "Cidade", "2:57", 177, 265f),
        Track("Café às Seis", "Marina Vale", "Rota 116", "3:33", 213, 275f),
    )

    val devices = listOf(
        PairedDevice("iPhone de Ana", Ph.DeviceMobile),
        PairedDevice("Galaxy S23 de Rafael", Ph.DeviceMobile),
        PairedDevice("Fone JBL Tune", Ph.Headphones),
    )

    val calls = listOf(
        RecentCall("MS", "Mãe", "Recebida · 14:20", Ph.PhoneIncoming),
        RecentCall("JP", "João Pedro", "Efetuada · Ontem", Ph.PhoneOutgoing),
        RecentCall("OF", "Oficina Honda", "Perdida · Ontem", Ph.PhoneX),
    )

    // ── derived ──────────────────────────────────────────────────────────────

    val track: Track get() = tracks[trackIndex]

    val connected: Boolean get() = btOn && connectedIndex != null

    val connectedName: String
        get() = connectedIndex?.takeIf { btOn }?.let { devices[it].name } ?: "Sem conexão"

    val sourceLabel: String
        get() = when (source) {
            Source.Bt -> "Bluetooth · " + if (connected) connectedName else "—"
            Source.Usb -> "USB · Pendrive"
            Source.Fm -> "Rádio FM · 98,5"
        }

    val btStatus: String
        get() = when {
            connected -> "Conectado"
            btOn -> "Disponível"
            else -> "Desligado"
        }

    val phoneMeta: String
        get() = if (connected) "Bateria 78% · Chamadas e áudio"
        else "Toque para conectar um celular"

    val progress: Float get() = elapsedSeconds.toFloat() / track.seconds

    val elapsedText: String get() = "%d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60)

    val timeText: String get() = now.format(TIME_FORMAT)

    val scanLabel: String
        get() = if (scanning) "Procurando dispositivos próximos…" else "Parear novo dispositivo"

    val scanIcon: Ph get() = if (scanning) Ph.CircleNotch else Ph.MagnifyingGlass

    // ── actions ──────────────────────────────────────────────────────────────

    fun go(to: Screen) { screen = to }

    fun volumeUp() { volume = (volume + 1).coerceAtMost(40) }

    fun volumeDown() { volume = (volume - 1).coerceAtLeast(0) }

    fun togglePlay() { playing = !playing }

    fun toggleShuffle() { shuffle = !shuffle }

    fun next() {
        trackIndex = (trackIndex + 1) % tracks.size
        elapsedSeconds = 0
    }

    /** Restart the track unless we are within the first few seconds. */
    fun previous() {
        if (elapsedSeconds > 3) {
            elapsedSeconds = 0
        } else {
            trackIndex = (trackIndex - 1 + tracks.size) % tracks.size
            elapsedSeconds = 0
        }
    }

    fun play(index: Int) {
        trackIndex = index
        elapsedSeconds = 0
        playing = true
    }

    fun toggleBluetooth() { btOn = !btOn }

    fun toggleDevice(index: Int) {
        connectedIndex = if (connectedIndex == index) null else index
    }

    /** One second of playback; also advances the clock. */
    fun tick() {
        now = LocalTime.now()
        if (!playing) return
        if (elapsedSeconds + 1 >= track.seconds) next() else elapsedSeconds++
    }

    private companion object {
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
