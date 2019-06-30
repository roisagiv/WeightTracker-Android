package io.roisagiv.github.weighttracker.api

import com.google.common.truth.Truth.assertThat
import io.roisagiv.github.weighttracker.utils.Resources
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test

class AirtableAPITest {

    @get:Rule
    var mockWebServer = MockWebServer()

    @Test
    fun recordsShouldReturnListOfWeightRecords() = runBlocking {
        // Arrange
        val client = AirtableAPI.build(
            mockWebServer.url("/").toString(),
            API_KEY
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Resources.read("records_success.json"))
        )

        // Act
        val clientResponse = client.records()

        // Assert
        assertThat(mockWebServer.requestCount).isEqualTo(1)
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/")
        assertThat(request.headers["Authorization"]).isEqualTo("Bearer $API_KEY")

        assertThat(clientResponse.isSuccessful).isTrue()

        val records = clientResponse.body()?.records ?: listOf()
        assertThat(records).hasSize(3)

        val record = records[0]
        assertThat(record.fields).hasSize(6)
        assertThat(record.fields["Weight"]).isEqualTo("80")
        assertThat(record.fields["Date"]).isEqualTo("2019-06-03T21:03:00.000Z")
    }

    @Test
    fun recordsShouldReturnFailureResponseInCaseOfServerError() = runBlocking {
        // Arrange
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val client =
            AirtableAPI.build(
                mockWebServer.url("/").toString(),
                API_KEY
            )

        // Act
        val clientResponse = client.records()

        // Assert
        assertThat(mockWebServer.requestCount).isEqualTo(1)
        assertThat(clientResponse.isSuccessful).isFalse()
        assertThat(clientResponse.body()).isNull()
    }

    @Test
    fun createRecordShouldReturnNewlyCreatedRecord() = runBlocking {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(Resources.read("create_record_success.json"))
        )
        val client =
            AirtableAPI.build(
                mockWebServer.url("/").toString(),
                API_KEY
            )

        // Act
        val clientResponse = client.create(
            CreateRecordBody(
                mapOf(
                    "Weight" to "11.1",
                    "Date" to "2019-06-03T21:03:00.000Z",
                    "UserName" to "fsdfsdf"
                )
            )
        )

        // Assert
        assertThat(mockWebServer.requestCount).isEqualTo(1)
        val request = mockWebServer.takeRequest()
        assertThat(request.method).isEqualTo("POST")
        assertThat(clientResponse.isSuccessful).isTrue()
        val record = clientResponse.body()
        assertThat(record?.id).isEqualTo("recx6FjYsKNY5R5sF")
        assertThat(record?.fields?.get("UserName")).isEqualTo("fsdfsdf")
    }

    companion object {
        const val API_KEY: String = "api_key"
    }
}
