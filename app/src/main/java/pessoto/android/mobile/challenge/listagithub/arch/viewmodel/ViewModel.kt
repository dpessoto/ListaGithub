package pessoto.android.mobile.challenge.listagithub.arch.viewmodel

import androidx.lifecycle.LiveData

abstract class ViewModel<uiState : UIState>(initialState: uiState) :
    androidx.lifecycle.ViewModel() {

    private val viewModelState = State(initialState)

    val state: LiveData<uiState> = viewModelState.state

    protected fun setState(state: (uiState) -> uiState) {
        viewModelState.setState(state)
    }
}