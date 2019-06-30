package io.roisagiv.github.weighttracker.history

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import io.roisagiv.github.weighttracker.R
import io.roisagiv.github.weighttracker.entity.WeightItem
import io.roisagiv.github.weighttracker.screenshot.ScreenshotRule
import io.roisagiv.github.weighttracker.utils.DisableAnimationsRule
import io.roisagiv.github.weighttracker.utils.RecyclerHelpers
import org.hamcrest.Matchers.greaterThan
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.threeten.bp.OffsetDateTime

@MediumTest
@RunWith(AndroidJUnit4::class)
class HistoryActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(
        HistoryActivity::class.java, false, false
    )

    @get:Rule
    val animationsRule = DisableAnimationsRule()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(WRITE_EXTERNAL_STORAGE)

    @get:Rule
    val screenshotRule = ScreenshotRule()

    private var viewModel: MockHistoryViewModel = MockHistoryViewModel()

    @Before
    fun before() {
        viewModel = MockHistoryViewModel()
        loadKoinModules(module {
            viewModel<HistoryViewModel> { viewModel }
        })
    }

    @Test
    fun successStateShouldDisplayListOfItems() {
        // Arrange
        activityRule.launchActivity(Intent())

        // Act
        viewModel.postViewState(createSuccessState())

        // Assert
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withId(R.id.recycler_history_items)).perform(
            RecyclerHelpers.waitUntil(
                RecyclerHelpers.hasItemCount(greaterThan(0))
            )
        )
        assertRecyclerViewItemCount(R.id.recycler_history_items, 5)
        screenshotRule.takeScreenshot()
    }

    @Test
    fun loadingStateShouldDisplayProgressBar() {
        // Arrange
        activityRule.launchActivity(Intent())

        // Act
        viewModel.postViewState(HistoryViewModel.ViewState.Loading)

        // Assert
        assertDisplayed(R.id.progressBar)
        screenshotRule.takeScreenshot()
    }

    private fun createSuccessState(): HistoryViewModel.ViewState.Success {
        val items: List<WeightItem> = listOf(
            WeightItem(
                id = "1",
                date = OffsetDateTime.parse("2019-06-12T21:00:00.000Z"),
                weight = 172.42,
                notes = ""
            ),
            WeightItem(
                id = "2",
                date = OffsetDateTime.parse("2019-06-11T21:00:00.000Z"),
                weight = 172.95,
                notes = ""
            ),
            WeightItem(
                id = "3",
                date = OffsetDateTime.parse("2019-06-10T21:00:00.000Z"),
                weight = 173.3,
                notes = ""
            ),
            WeightItem(
                id = "4",
                date = OffsetDateTime.parse("2019-06-09T21:00:00.000Z"),
                weight = 173.9,
                notes = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
            ),
            WeightItem(
                id = "5",
                date = OffsetDateTime.parse("2019-06-08T21:00:00.000Z"),
                weight = 173.5,
                notes = """Lorem ipsum dolor sit amet, consectetur adipiscing elit, 
                    |sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. 
                    |Ut enim ad minim veniam, 
                    |quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
                    |Duis aute irure dolor in reprehenderit in voluptate velit 
                    |esse cillum dolore eu fugiat nulla pariatur. 
                    |Excepteur sint occaecat cupidatat non proident, 
                    |sunt in culpa qui officia deserunt mollit anim id est laborum""".trimMargin()
            )
        )
        return HistoryViewModel.ViewState.Success(items)
    }

    class MockHistoryViewModel : HistoryViewModel() {
        private val internalState: MutableLiveData<ViewState> = MutableLiveData()

        override val state: LiveData<ViewState> = internalState

        override fun refresh() = Unit

        fun postViewState(viewState: ViewState) {
            internalState.postValue(viewState)
        }
    }
}
