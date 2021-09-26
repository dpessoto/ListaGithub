package pessoto.android.mobile.challenge.listagithub.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Items(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("owner") val owner: Owner,
    @SerializedName("stargazers_count") val stars: Long,
    @SerializedName("forks") val forks: Long,
    @SerializedName("html_url") val urlRepository: String,
    var showLoading: Boolean = false
) : Serializable