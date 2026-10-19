/*
 * ************************************************************************
 *  GlassTheme.kt
 * *************************************************************************
 * Optional "Glass" and "Liquid glass" appearance. GPLv2+ like the rest of VLC.
 * *************************************************************************
 */

package org.videolan.vlc.gui.helpers

import android.content.Context
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import org.videolan.tools.KEY_GLASS_THEME
import org.videolan.tools.Settings
import org.videolan.vlc.R

const val GLASS_THEME_OFF = "0"
const val GLASS_THEME_GLASS = "1"
const val GLASS_THEME_LIQUID = "2"

/**
 * @return the selected glass mode: [GLASS_THEME_OFF], [GLASS_THEME_GLASS] or [GLASS_THEME_LIQUID]
 */
fun Context.glassMode(): String =
    Settings.getInstance(this).getString(KEY_GLASS_THEME, GLASS_THEME_OFF) ?: GLASS_THEME_OFF

/**
 * Applies the glass theme overlay on top of the current theme.
 * Must be called before the views are inflated (it is called from applyTheme()).
 */
fun AppCompatActivity.applyGlassTheme() {
    val mode = glassMode()
    if (mode == GLASS_THEME_OFF) return
    val liquid = mode == GLASS_THEME_LIQUID
    theme.applyStyle(if (liquid) R.style.ThemeOverlay_VLC_LiquidGlass else R.style.ThemeOverlay_VLC_Glass, true)
    window.setBackgroundDrawableResource(if (liquid) R.drawable.liquid_window_bg else R.drawable.glass_window_bg)
}

/**
 * Blurs what is behind a translucent window (bottom sheets). Needs Android 12+ and
 * system blur enabled, otherwise it does nothing and the translucent fill is used.
 */
fun Window.applyGlassBlur(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    val mode = context.glassMode()
    if (mode == GLASS_THEME_OFF) return
    try {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (!wm.isCrossWindowBlurEnabled) return
        setBackgroundBlurRadius(if (mode == GLASS_THEME_LIQUID) 48 else 28)
    } catch (e: Throwable) {
        // blur is cosmetic only
    }
}
