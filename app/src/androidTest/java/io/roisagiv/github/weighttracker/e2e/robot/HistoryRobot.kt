package io.roisagiv.github.weighttracker.e2e.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.internal.matcher.RecyclerViewItemCountAssertion
import io.roisagiv.github.weighttracker.R

class HistoryRobot(private val uiDevice: UiDevice) {

    fun assertPageDisplayed() {
        assertDisplayed(R.string.app_name)
    }

    fun assertNumberOfItemsInList(expected: Int) {
        uiDevice.findObject(UiSelector().textContains("Test Node 1"))
            .waitForExists(500)

        onView(withContentDescription(R.string.content_description_history_list))
            .check(RecyclerViewItemCountAssertion(expected))
    }

    fun navigateToAddWeight() {
        onView(withContentDescription(R.string.content_description_add_weight))
            .perform(click())
    }
}
