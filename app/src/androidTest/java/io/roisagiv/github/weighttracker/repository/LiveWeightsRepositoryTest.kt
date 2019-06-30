package io.roisagiv.github.weighttracker.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import io.roisagiv.github.weighttracker.api.AirtableAPI
import io.roisagiv.github.weighttracker.entity.WeightItem
import io.roisagiv.github.weighttracker.entity.NewWeightItem
import io.roisagiv.github.weighttracker.db.WeightsDatabaseRule
import io.roisagiv.github.weighttracker.utils.Assets
import io.roisagiv.github.weighttracker.utils.Resource
import io.roisagiv.github.weighttracker.utils.Status
import io.roisagiv.github.weighttracker.utils.TestObserver
import io.roisagiv.github.weighttracker.utils.test
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.OffsetDateTime

@RunWith(AndroidJUnit4::class)
class LiveWeightsRepositoryTest {

    @get:Rule
    var mockWebServer = MockWebServer()

    @get:Rule
    var weightsDatabase = WeightsDatabaseRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun givenSavedItemsAllShouldUpdateDB() {
        // Arrange
        val body = Assets.read(
            "records_success.json",
            InstrumentationRegistry.getInstrumentation().context
        )
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val client = AirtableAPI.build(
            mockWebServer.url("").toString(), API_KEY
        )

        val weightItemsDao = weightsDatabase.weightItemsDao
        runBlocking {
            weightItemsDao.save(
                WeightItem(
                    id = "recVzvGP6aXci0Lly",
                    date = OffsetDateTime.parse("2019-06-12T21:00:00.000Z"),
                    weight = 172.42,
                    notes = ""
                )
            )
        }
        val repository = LiveWeightsRepository(client, weightItemsDao)
        var observer: TestObserver<Resource<List<WeightItem>>>? = null

        // Act
        runBlocking {
            observer = repository.allItems().test(2)
        }

        // Assert
        assertThat(mockWebServer.takeRequest()).isNotNull()
        assertThat(mockWebServer.requestCount).isEqualTo(1)
        observer?.await()
        observer?.assertValues {
            assertThat(it[0].status).isEqualTo(Status.LOADING)
            assertThat(it[0].data).hasSize(1)
            assertThat(it[1].status).isEqualTo(Status.SUCCESS)
            assertThat(it[1].data).hasSize(3)
        }
    }

    @Test
    fun givenCleanStateAllShouldCallNetworkAndSaveInDB() {
        // Arrange
        val body = Assets.read(
            "records_success.json",
            InstrumentationRegistry.getInstrumentation().context
        )
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(body)
        )
        val client = AirtableAPI.build(
            mockWebServer.url("").toString(), API_KEY
        )
        val weightItemsDao = weightsDatabase.weightItemsDao
        var observer: TestObserver<Resource<List<WeightItem>>>? = null
        val repository = LiveWeightsRepository(client, weightItemsDao)
        // Act
        runBlocking {
            observer = repository.allItems().test(2)
        }
        // Assert
        assertThat(mockWebServer.takeRequest()).isNotNull()
        assertThat(mockWebServer.requestCount).isEqualTo(1)
        observer?.await()
        observer?.assertValues {
            assertThat(it[0].status).isEqualTo(Status.LOADING)
            assertThat(it[0].data).hasSize(0)
            assertThat(it[1].status).isEqualTo(Status.SUCCESS)
            assertThat(it[1].data).hasSize(3)
        }
    }

    @Test
    fun saveShouldPerformNetworkAndSaveInDB() {
        // Arrange
        val body = Assets.read(
            "create_record_success.json",
            InstrumentationRegistry.getInstrumentation().context
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(body)
        )
        val client = AirtableAPI.build(
            mockWebServer.url("").toString(), API_KEY
        )
        var observer: TestObserver<Resource<WeightItem?>>? = null
        val historyDataItemDao = weightsDatabase.weightItemsDao
        val repository = LiveWeightsRepository(client, historyDataItemDao)

        // Act
        runBlocking {
            observer = repository
                .save(NewWeightItem(OffsetDateTime.now(), 10.0, null))
                .test(2)
        }
        // Assert
        assertThat(mockWebServer.takeRequest()).isNotNull()
        assertThat(mockWebServer.requestCount).isEqualTo(1)
        observer?.await()
        observer?.assertValues {
            assertThat(it[0].status).isEqualTo(Status.LOADING)
            assertThat(it[0].data).isNull()
            assertThat(it[1].status).isEqualTo(Status.SUCCESS)
            assertThat(it[1].data).isNull()
        }

        runBlocking {
            assertThat(historyDataItemDao.all()).hasSize(1)
        }
    }

    companion object {
        const val API_KEY = "API_KEY"
    }
}
