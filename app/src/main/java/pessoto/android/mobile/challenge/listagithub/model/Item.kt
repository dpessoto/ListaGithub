package pessoto.android.mobile.challenge.listagithub.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Item(
    @SerializedName("full_name") val fullName: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("owner") val owner: Owner = Owner(),
    @SerializedName("stargazers_count") val stars: Long = 0,
    @SerializedName("forks") val forks: Long = 0,
    @SerializedName("html_url") val urlRepository: String = "",
    var error: Error = Error()
) : Serializable {
    data class Error(
        var showLoading: Boolean = false,
        var tryAgain: Boolean = false,
        var tryAgainMessage: String = ""
    ) : Serializable
}