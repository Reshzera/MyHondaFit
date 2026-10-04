package com.example.myhondafit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.myhondafit.shell.HeadUnitShell

/**
 * Host for the head-unit shell. The shell owns the whole display — no Material
 * scaffold, no system insets, no app chrome. On the target image any remaining
 * system bars are already switched off by `oem/overlays/systembars`; hiding them
 * here keeps the prototype honest on a stock device too.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent { HeadUnitShell() }
    }
}
