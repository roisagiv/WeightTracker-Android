package io.roisagiv.github.weighttracker.repository

import androidx.lifecycle.LiveData
import com.github.ajalt.timberkt.Timber
import io.roisagiv.github.weighttracker.api.AirtableAPI
import io.roisagiv.github.weighttracker.api.AirtableRecord
import io.roisagiv.github.weighttracker.api.CreateRecordBody
import io.roisagiv.github.weighttracker.api.RecordsApiResponse
import io.roisagiv.github.weighttracker.db.DateConverters
import io.roisagiv.github.weighttracker.db.WeightItemsDao
import io.roisagiv.github.weighttracker.entity.NewWeightItem
import io.roisagiv.github.weighttracker.entity.WeightItem
import io.roisagiv.github.weighttracker.utils.NetworkBoundResource
import io.roisagiv.github.weighttracker.utils.Resource
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Response

interface WeightsRepository {
    suspend fun allItems(): LiveData<Resource<List<WeightItem>>>
    suspend fun save(item: NewWeightItem): LiveData<Resource<WeightItem?>>
}

class LiveWeightsRepository(
    private val airtableAPI: AirtableAPI,
    private val weightItemsDao: WeightItemsDao
) : WeightsRepository {
    /**
     *
     */
    override suspend fun allItems(): LiveData<Resource<List<WeightItem>>> {
        return object :
            NetworkBoundResource<List<WeightItem>, RecordsApiResponse>() {
            /**
             *
             */
            override suspend fun createCall(): Response<RecordsApiResponse> =
                airtableAPI.records()

            override fun processResponse(response: RecordsApiResponse): List<WeightItem> =
                response.records.map {
                    WeightItem(
                        it.id,
                        date = OffsetDateTime.parse(
                            it.fields["Date"],
                            DateTimeFormatter.ISO_OFFSET_DATE_TIME
                        ),
                        weight = it.fields["Weight"]?.toDouble() ?: 0.0,
                        notes = it.fields["Notes"] ?: ""
                    )
                }

            override suspend fun saveCallResults(items: List<WeightItem>) {
                Timber.d { "results = $items" }
                for (item in items) {
                    weightItemsDao.save(item)
                }
            }

            override fun shouldFetch(data: List<WeightItem>?): Boolean = true

            override suspend fun loadFromDb(): List<WeightItem> = weightItemsDao.all()
        }.build().asLiveData()
    }

    /**
     *
     */
    override suspend fun save(item: NewWeightItem): LiveData<Resource<WeightItem?>> {
        return object : NetworkBoundResource<WeightItem?, AirtableRecord>() {
            override fun processResponse(response: AirtableRecord): WeightItem? {
                return WeightItem(
                    response.id,
                    DateConverters.toOffsetDateTime(response.fields["Date"])
                        ?: OffsetDateTime.now(),
                    response.fields["Weight"]?.toDouble() ?: Double.NaN,
                    response.fields["Notes"] ?: ""
                )
            }

            override suspend fun saveCallResults(items: WeightItem?) {
                items?.let {
                    weightItemsDao.save(it)
                }
            }

            override fun shouldFetch(data: WeightItem?): Boolean = true

            override suspend fun loadFromDb(): WeightItem? = null

            override suspend fun createCall(): Response<AirtableRecord> {
                return airtableAPI.create(
                    CreateRecordBody(
                        mapOf(
                            "Weight" to item.weight,
                            "Date" to DateConverters.fromOffsetDateTime(item.date)!!
                        )
                    )
                )
            }
        }.build().asLiveData()
    }
}
