package io.roisagiv.github.weighttracker.e2e.airtable

import com.google.gson.JsonObject
import io.roisagiv.github.weighttracker.api.ApiKeyInterceptor
import io.roisagiv.github.weighttracker.api.RecordsApiResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AirtableBatchAPI {

    @GET("./")
    suspend fun records(): Response<RecordsApiResponse>

    @POST("./")
    suspend fun create(@Body body: JsonObject): Response<Unit>

    @DELETE("./")
    suspend fun delete(@Query("records[]") records: List<String>): Response<Unit>

    companion object {

        fun build(baseUrl: String, apiKey: String): AirtableBatchAPI {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor(apiKey))
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create()
        }
    }
}
