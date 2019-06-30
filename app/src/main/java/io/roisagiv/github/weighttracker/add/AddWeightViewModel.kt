package io.roisagiv.github.weighttracker.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import io.roisagiv.github.weighttracker.entity.NewWeightItem
import io.roisagiv.github.weighttracker.repository.WeightsRepository
import io.roisagiv.github.weighttracker.utils.Status
import kotlinx.coroutines.Dispatchers
import org.threeten.bp.OffsetDateTime

abstract class AddWeightViewModel : ViewModel() {

    abstract val state: LiveData<ViewState>

    abstract fun add(date: OffsetDateTime, weight: Double, notes: String?)

    sealed class ViewState {
        object Idle : ViewState()
        object Loading : ViewState()
        class Error(val error: Throwable? = null) : ViewState()
        object Success : ViewState()
    }
}

class LiveAddWeightViewModel(private val repository: WeightsRepository) : AddWeightViewModel() {
    private val addAction = MutableLiveData<NewWeightItem>()

    override val state: LiveData<ViewState> = addAction.switchMap { newItem ->
        liveData<ViewState>(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(repository.save(newItem).map {
                when (it.status) {
                    Status.LOADING -> ViewState.Loading
                    Status.SUCCESS -> ViewState.Success
                    Status.ERROR -> ViewState.Error()
                }
            })
        }
    }

    override fun add(date: OffsetDateTime, weight: Double, notes: String?) {
        addAction.postValue(NewWeightItem(date, weight, notes))
    }
}
