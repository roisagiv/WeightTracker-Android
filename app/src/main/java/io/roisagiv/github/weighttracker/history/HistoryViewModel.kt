package io.roisagiv.github.weighttracker.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import io.roisagiv.github.weighttracker.entity.WeightItem
import io.roisagiv.github.weighttracker.repository.WeightsRepository
import io.roisagiv.github.weighttracker.utils.Status
import kotlinx.coroutines.Dispatchers

/**
 *
 */
abstract class HistoryViewModel : ViewModel() {
    abstract val state: LiveData<ViewState>

    abstract fun refresh()

    sealed class ViewState {
        object Loading : ViewState()
        data class Error(val error: Throwable? = null) : ViewState()
        data class Success(val list: List<WeightItem>) : ViewState()
    }
}

class LiveHistoryViewModel(private val repository: WeightsRepository) :
    HistoryViewModel() {

    private val refreshAction: MutableLiveData<Unit> = MutableLiveData()

    override val state: LiveData<ViewState> = refreshAction.switchMap {
        liveData<ViewState>(
            context = viewModelScope.coroutineContext + Dispatchers.IO
        ) {
            emitSource(repository.allItems().map {
                when (it.status) {
                    Status.LOADING -> ViewState.Loading
                    Status.SUCCESS -> ViewState.Success(it.data!!)
                    Status.ERROR -> ViewState.Error()
                }
            })
        }
    }

    override fun refresh() {
        refreshAction.postValue(Unit)
    }
}
