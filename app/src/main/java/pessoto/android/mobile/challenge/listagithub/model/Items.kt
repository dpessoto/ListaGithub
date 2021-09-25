package pessoto.android.mobile.challenge.listagithub.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Items(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("description") val description: String,
    @SerializedName("owner") val owner: Owner,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks") val forks: Int,
    @SerializedName("html_url") val urlRepository: String
) : Serializable