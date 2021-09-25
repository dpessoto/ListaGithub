package pessoto.android.mobile.challenge.listagithub.feature.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import pessoto.android.mobile.challenge.listagithub.R
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.activity.ListRepositoriesActivity
import pessoto.android.mobile.challenge.listagithub.util.view.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            startActivity(Intent(this, ListRepositoriesActivity::class.java))
            finish()
        }, 1000)
    }
}