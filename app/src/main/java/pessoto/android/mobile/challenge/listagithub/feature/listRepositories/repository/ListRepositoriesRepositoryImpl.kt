package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository

import pessoto.android.mobile.challenge.listagithub.data.network.DataSourceRemote
import pessoto.android.mobile.challenge.listagithub.model.Result
import pessoto.android.mobile.challenge.listagithub.model.ResultRepository

class ListRepositoriesRepositoryImpl : ListRepositoriesRepository {

    override suspend fun getRepositories(
        language: String,
        page: Int
    ): ResultRepository<Result> {
        return try {
            ResultRepository.Success(DataSourceRemote().getRepositories(language, page))
        } catch (e: Exception) {
            ResultRepository.Error(e)
        }
    }
}