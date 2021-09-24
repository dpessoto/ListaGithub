package pessoto.android.mobile.challenge.listagithub.model

import com.google.gson.annotations.SerializedName

data class Result(@SerializedName("items") val items: ArrayList<Items>)