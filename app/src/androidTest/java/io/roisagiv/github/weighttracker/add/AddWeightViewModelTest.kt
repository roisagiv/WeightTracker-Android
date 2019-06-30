package io.roisagiv.github.weighttracker.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import io.roisagiv.github.weighttracker.api.AirtableAPI
import io.roisagiv.github.weighttracker.db.WeightsDatabaseRule
import io.roisagiv.github.weighttracker.repository.LiveWeightsRepository
import io.roisagiv.github.weighttracker.utils.Assets
import io.roisagiv.github.weighttracker.utils.test
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.OffsetDateTime

@RunWith(AndroidJUnit4::class)
class AddWeightViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mockWebServer = MockWebServer()

    @get:Rule
    var weightsDatabase = WeightsDatabaseRule()

    private lateinit var viewModel: LiveAddWeightViewModel

    @Before
    fun beforeEach() {
        val client =
            AirtableAPI.build(mockWebServer.url("").toString(), "")

        val repository = LiveWeightsRepository(client, weightsDatabase.weightItemsDao)
        viewModel = LiveAddWeightViewModel(repository)
    }

    @Test
    fun addWeightShouldCallNetworkAndSaveInDB() {
        // Arrange
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(
                    Assets.read(
                        "create_record_success.json",
                        InstrumentationRegistry.getInstrumentation().context
                    )
                )
        )

        // Act
        val observer = viewModel.state.test(2)
        viewModel.add(OffsetDateTime.now(), 10.0, "Notes")
        observer.await()

        // Assert
        assertThat(mockWebServer.requestCount).isEqualTo(1)
        observer.assertValues {
            assertThat(it).hasSize(2)
            assertThat(it[0]).isSameInstanceAs(AddWeightViewModel.ViewState.Loading)
            assertThat(it[1]).isSameInstanceAs(AddWeightViewModel.ViewState.Success)
        }
        runBlocking {
            assertThat(weightsDatabase.weightItemsDao.all()).hasSize(1)
        }
    }
}
