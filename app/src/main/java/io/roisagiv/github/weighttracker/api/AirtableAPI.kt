package io.roisagiv.github.weighttracker.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AirtableAPI {

    @GET("./")
    suspend fun records(): Response<RecordsApiResponse>

    @POST("./")
    suspend fun create(@Body fields: CreateRecordBody): Response<AirtableRecord>

    companion object {

        fun build(baseUrl: String, apiKey: String): AirtableAPI {
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

        fun build(
            baseUrl: String,
            okHttpClient: OkHttpClient
        ): AirtableAPI {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create()
        }
    }
}

data class RecordsApiResponse(val records: List<AirtableRecord>)

data class AirtableRecord(val id: String, val fields: Map<String, String>)

data class CreateRecordBody(val fields: Map<String, Any>)
