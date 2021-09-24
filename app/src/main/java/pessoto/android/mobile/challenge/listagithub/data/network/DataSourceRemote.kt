package pessoto.android.mobile.challenge.listagithub.data.network

class DataSourceRemote {
    private val service = NetworkClient().service()

    suspend fun getRepositories(language: String, page: Int) =
        service.getRepositories("language:$language", "stars", page)
}