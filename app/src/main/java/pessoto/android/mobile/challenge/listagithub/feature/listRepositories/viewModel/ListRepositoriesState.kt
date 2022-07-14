package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.viewModel

import pessoto.android.mobile.challenge.listagithub.arch.viewmodel.UIState
import pessoto.android.mobile.challenge.listagithub.model.Item
import pessoto.android.mobile.challenge.listagithub.model.Result

data class ListRepositoriesState(
    val isLoading: Boolean = false,
    val list: List<Item> = emptyList(),
    val error: Throwable? = null
) : UIState
