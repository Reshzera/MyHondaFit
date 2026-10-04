package com.example.myhondafit.shell

import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Head-unit previews for the Studio Design pane.
 *
 * The emulator panel is 1408×792 at density 160, so 1 dp == 1 px there and these
 * numbers match the real display. Each preview boots the whole shell — status
 * bar and rail included — on one screen, so what the pane renders is what the
 * unit renders.
 *
 * Use the Design/Split toggle at the top right of the editor to see them, and
 * the "Start Interactive Preview" (pointer) button on a preview to click through
 * it without deploying.
 */
private const val W = 1408
private const val H = 792

@Preview(name = "1 · Início", widthDp = W, heightDp = H)
@Composable
private fun HomePreview() = PreviewShell(Screen.Home)

@Preview(name = "2 · Mapa", widthDp = W, heightDp = H)
@Composable
private fun MapPreview() = PreviewShell(Screen.Map)

@Preview(name = "3 · Música", widthDp = W, heightDp = H)
@Composable
private fun MusicPreview() = PreviewShell(Screen.Music)

@Preview(name = "4 · Bluetooth", widthDp = W, heightDp = H)
@Composable
private fun BluetoothPreview() = PreviewShell(Screen.Phone)

/** Bluetooth off — the empty state the design specifies for that case. */
@Preview(name = "4b · Bluetooth desligado", widthDp = W, heightDp = H)
@Composable
private fun BluetoothOffPreview() = PreviewShell(Screen.Phone) { btOn = false }

@Composable
private fun PreviewShell(screen: Screen, setup: HeadUnitState.() -> Unit = {}) {
    HeadUnitShell(
        remember {
            HeadUnitState().apply {
                this.screen = screen
                setup()
            }
        }
    )
}
