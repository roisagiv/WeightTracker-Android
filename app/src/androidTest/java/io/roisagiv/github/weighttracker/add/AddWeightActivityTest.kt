package io.roisagiv.github.weighttracker.add

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.google.common.truth.Truth.assertThat
import com.schibsted.spain.barista.assertion.BaristaEnabledAssertions.assertDisabled
import com.schibsted.spain.barista.assertion.BaristaEnabledAssertions.assertEnabled
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.schibsted.spain.barista.interaction.BaristaKeyboardInteractions.closeKeyboard
import io.roisagiv.github.weighttracker.R
import io.roisagiv.github.weighttracker.screenshot.ScreenshotRule
import io.roisagiv.github.weighttracker.utils.DisableAnimationsRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.threeten.bp.OffsetDateTime

@MediumTest
@RunWith(AndroidJUnit4::class)
class AddWeightActivityTest : KoinTest {
    @get:Rule
    val activityRule = ActivityTestRule(
        AddWeightActivity::class.java, false, false
    )

    @get:Rule
    val animationsRule = DisableAnimationsRule()

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @get:Rule
    val screenshotRule = ScreenshotRule()

    private var viewModel: MockAddWeightViewModel = MockAddWeightViewModel()

    @Before
    fun before() {
        viewModel = MockAddWeightViewModel()
        loadKoinModules(module(override = true) {
            viewModel<AddWeightViewModel> { viewModel }
        })
    }

    @Test
    fun onIdleButtonShouldBeEnabled() {
        // Arrange
        activityRule.launchActivity(Intent())

        // Act
        activityRule.runOnUiThread {
            viewModel.internalState.value = AddWeightViewModel.ViewState.Idle
        }

        // Assert
        assertEnabled(R.id.button_save)
        screenshotRule.takeScreenshot()
    }

    @Test
    fun onLoadingButtonShouldBeDisabled() {
        // Arrange
        activityRule.launchActivity(Intent())

        // Act
        activityRule.runOnUiThread {
            viewModel.internalState.value = AddWeightViewModel.ViewState.Loading
        }

        // Assert
        assertDisabled(R.id.button_save)
        screenshotRule.takeScreenshot()
    }

    @Test
    fun onSuccessShouldFinishActivity() {
        // Arrange
        activityRule.launchActivity(Intent())

        // Act
        activityRule.runOnUiThread {
            viewModel.internalState.value = AddWeightViewModel.ViewState.Success
        }

        // Assert
        assertThat(activityRule.activity.isFinishing).isTrue()
    }

    @Test
    fun clickSaveShouldCallViewModel() {
        // Arrange
        activityRule.launchActivity(Intent())

        writeTo(R.id.edittext_weight, "55.5")
        closeKeyboard()

        // Act
        clickOn(R.id.button_save)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // Assert
        assertThat(viewModel.addCalled).isTrue()
    }

    /**
     *
     */
    class MockAddWeightViewModel : AddWeightViewModel() {
        internal var addCalled: Boolean = false
        internal val internalState = MutableLiveData<ViewState>()

        override val state: LiveData<ViewState> = internalState

        override fun add(date: OffsetDateTime, weight: Double, notes: String?) {
            addCalled = true
        }
    }
}
