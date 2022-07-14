package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pessoto.android.mobile.challenge.listagithub.arch.viewmodel.ViewModel
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepository
import pessoto.android.mobile.challenge.listagithub.model.Result
import pessoto.android.mobile.challenge.listagithub.model.ResultRepository
import pessoto.android.mobile.challenge.listagithub.model.StateView

class ListRepositoriesViewModel(private val repository: ListRepositoriesRepository) :
    ViewModel<ListRepositoriesState>(ListRepositoriesState()) {
    private val _stateView = MutableLiveData<StateView<Result>>()
    val stateView: LiveData<StateView<Result>>
        get() = _stateView

    fun getRepositories(language: String, page: Int) {
        viewModelScope.launch {
//            _stateView.value = StateView.Loading
            setState { it.copy(isLoading = true) }

            when (val result = repository.getRepositories(language, page)) {
                is ResultRepository.Success -> {
                    _stateView.value = StateView.DataLoaded(result.data)
                    setState {
                        it.copy(list = result.data.items)
                    }
                }
                is ResultRepository.Error -> {
//                    _stateView.value = StateView.Error(result.exception)
                }
            }
        }
    }
}