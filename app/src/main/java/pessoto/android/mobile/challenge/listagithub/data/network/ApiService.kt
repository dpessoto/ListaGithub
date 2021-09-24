package pessoto.android.mobile.challenge.listagithub.data.network

import pessoto.android.mobile.challenge.listagithub.model.Result
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/search/repositories")
    suspend fun getRepositories(
        @Query("q") language: String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): Result
}