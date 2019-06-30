package io.roisagiv.github.weighttracker.screenshot

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import java.util.Locale

object DeviceIdentifier {

    data class ScreenMetrics(val width: Int, val height: Int, val density: Float)

    fun getDemiensions(context: Context): ScreenMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        val display = windowManager.defaultDisplay

        display.getRealMetrics(metrics)

        return ScreenMetrics(metrics.widthPixels, metrics.heightPixels, metrics.density)
    }

    val androidVersion: String = android.os.Build.VERSION.SDK_INT.toString()

    val language: String = Locale.getDefault().language
}
