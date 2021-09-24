package pessoto.android.mobile.challenge.listagithub.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Items(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("description") val description: String
) : Serializable