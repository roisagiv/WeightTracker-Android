package io.roisagiv.github.weighttracker.e2e.robot

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry

object ApplicationRobot {
    fun launchApp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        context.startActivity(intent)
    }
}
