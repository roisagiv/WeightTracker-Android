package io.roisagiv.github.weighttracker.utils

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.coroutineContext

abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor() {
    private val result = MutableLiveData<Resource<ResultType>>()
    private val supervisorJob = SupervisorJob()

    suspend fun build(): NetworkBoundResource<ResultType, RequestType> {
        CoroutineScope(coroutineContext).launch(supervisorJob) {
            val dbResult = loadFromDb()
            if (shouldFetch(dbResult)) {
                try {
                    fetchFromNetwork(dbResult)
                } catch (e: Throwable) {
                    Timber.e { "Error $e" }
                    setValue(Resource.error(e.localizedMessage, loadFromDb()))
                }
            } else {
                Timber.d { "Return data from local database" }
                setValue(Resource.success(dbResult))
            }
        }
        return this
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    // ---

    private suspend fun fetchFromNetwork(dbResult: ResultType) {
        setValue(Resource.loading(dbResult)) // Dispatch latest value quickly (UX purpose)
        val apiResponse = createCall()
        if (apiResponse.isSuccessful) {
            apiResponse.body()?.let { saveCallResults(processResponse(it)) }
            setValue(Resource.success(loadFromDb()))
        } else {
            setValue(Resource.error(apiResponse.message(), loadFromDb()))
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        Timber.d { "Resource: $newValue" }
        if (result.value != newValue) {
            result.postValue(newValue)
        }
    }

    @WorkerThread
    protected abstract fun processResponse(response: RequestType): ResultType

    @WorkerThread
    protected abstract suspend fun saveCallResults(items: ResultType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract suspend fun loadFromDb(): ResultType

    @MainThread
    protected abstract suspend fun createCall(): Response<RequestType>
}
