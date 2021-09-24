package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository

import pessoto.android.mobile.challenge.listagithub.model.Result
import pessoto.android.mobile.challenge.listagithub.model.ResultRepository

interface ListRepositoriesRepository {
    suspend fun getRepositories(language: String, page: Int) : ResultRepository<Result>
}