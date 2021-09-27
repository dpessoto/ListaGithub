package pessoto.android.mobile.challenge.listagithub.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Owner(
    @SerializedName("avatar_url") val urlAvatar: String = "",
    @SerializedName("login") val login: String = "",
    @SerializedName("html_url") val urlOwner: String = ""
) : Serializable