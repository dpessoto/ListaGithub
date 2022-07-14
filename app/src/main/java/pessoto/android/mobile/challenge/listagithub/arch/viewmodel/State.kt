package pessoto.android.mobile.challenge.listagithub.arch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class State<uiState : UIState>(initialState: uiState) {

    private val _state = MutableLiveData(initialState)
    val state: LiveData<uiState> = _state

    fun setState(state: (uiState) -> uiState) {
        _state.value = state.invoke(_state.value!!)
    }
}