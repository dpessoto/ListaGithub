package pessoto.android.mobile.challenge.listagithub

import android.content.Context
import android.content.Intent
import android.net.Uri

class Router {

    companion object {
        val instance = Router()
    }

    fun goToWeb(context: Context, url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

}