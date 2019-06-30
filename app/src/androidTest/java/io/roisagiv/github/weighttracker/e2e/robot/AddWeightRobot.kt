package io.roisagiv.github.weighttracker.e2e.robot

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import io.roisagiv.github.weighttracker.R

class AddWeightRobot(private val uiDevice: UiDevice) {

    fun assertPageDisplayed() {
        uiDevice.findObject(UiSelector().textContains("SAVE"))
            .waitForExists(5000)
        assertDisplayed("SAVE")
    }

    fun typeWeight(weight: String) {
        onView(withContentDescription(R.string.content_description_weight))
            .perform(typeText(weight))
        closeSoftKeyboard()
    }

    fun save() {
        onView(withContentDescription(R.string.content_description_save_button))
            .perform(click())
    }
}
