package io.roisagiv.github.weighttracker.screenshot

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class ScreenshotRule : TestWatcher() {
    private var description: Description = Description.EMPTY

    fun takeScreenshot() {

        val (width, height, density) = DeviceIdentifier.getDemiensions(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        val parentFolderPath =
            "api${DeviceIdentifier.androidVersion}-${width}x${height}x$density-${DeviceIdentifier.language}"
        val currentScreenShotName = "${description.testClass.simpleName}-${description.methodName}"
        capture(parentFolderPath, currentScreenShotName)
    }

    private fun capture(parentFolderPath: String = "", fileName: String) {
        val screenCapture = Screenshot.capture()
        val processors = setOf(ScreenCaptureProcessor(parentFolderPath))
        screenCapture.apply {
            name = fileName
            process(processors)
        }
    }

    override fun starting(d: Description?) {
        description = d ?: Description.EMPTY
        super.starting(d)
    }
}
