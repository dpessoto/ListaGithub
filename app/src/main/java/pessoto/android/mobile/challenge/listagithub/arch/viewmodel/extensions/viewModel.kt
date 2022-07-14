package pessoto.android.mobile.challenge.listagithub.arch.viewmodel.extensions

import androidx.appcompat.app.AppCompatActivity
import pessoto.android.mobile.challenge.listagithub.arch.viewmodel.UIState
import pessoto.android.mobile.challenge.listagithub.arch.viewmodel.ViewModel

fun <uiState : UIState> AppCompatActivity.onStateChange(
    viewModel: ViewModel<uiState>,
    handleState: (uiState) -> Unit
) {
    viewModel.state.observe(this) { state -> handleState(state as uiState) }
}