package io.roisagiv.github.weighttracker.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import io.roisagiv.github.weighttracker.api.AirtableAPI
import io.roisagiv.github.weighttracker.db.WeightsDatabaseRule
import io.roisagiv.github.weighttracker.repository.LiveWeightsRepository
import io.roisagiv.github.weighttracker.utils.Assets
import io.roisagiv.github.weighttracker.utils.test
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class HistoryViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mockWebServer = MockWebServer()

    @get:Rule
    var weightsDatabase = WeightsDatabaseRule()

    @Test
    fun refreshShouldFetchFromServer() {
        // Arrange
        val body = Assets.read(
            "records_success.json",
            InstrumentationRegistry.getInstrumentation().context
        )
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(body))
        val client = AirtableAPI.build(mockWebServer.url("").toString(), API_KEY)

        val repository = LiveWeightsRepository(client, weightsDatabase.weightItemsDao)
        val viewModel = LiveHistoryViewModel(repository)

        // Act
        val observer = viewModel.state.test(expectedCount = 2)
        viewModel.refresh()
        observer.await()

        // Assert
        assertThat(mockWebServer.requestCount).isEqualTo(1)
        observer.assertValues {
            assertThat(it).hasSize(2)
            assertThat(it[0]).isSameInstanceAs(HistoryViewModel.ViewState.Loading)
            val items = it[1] as? HistoryViewModel.ViewState.Success
            assertThat(items?.list).hasSize(3)
            assertThat(items?.list?.get(0)?.id).isEqualTo("recx6FjYsKNY5R5sF")
            assertThat(items?.list?.get(0)?.date).isEqualTo(
                OffsetDateTime.parse(
                    "2019-06-03T21:03:00.000Z",
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                )
            )
        }
    }

    @Test
    fun givenServerErrorShouldReturnError() {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(400))
        val client = AirtableAPI.build(mockWebServer.url("").toString(), API_KEY)

        val repository = LiveWeightsRepository(client, weightsDatabase.weightItemsDao)
        val viewModel = LiveHistoryViewModel(repository)

        // Act
        val observer = viewModel.state.test(expectedCount = 2)
        viewModel.refresh()

        // Assert
        observer.await()
        assertThat(mockWebServer.requestCount).isEqualTo(1)
        observer.assertValues {
            assertThat(it).hasSize(2)
            assertThat(it[0]).isSameInstanceAs(HistoryViewModel.ViewState.Loading)
            val error = it[1] as? HistoryViewModel.ViewState.Error
            assertThat(error).isNotNull()
        }
    }

    companion object {
        const val API_KEY = "API_KEY"
    }
}
