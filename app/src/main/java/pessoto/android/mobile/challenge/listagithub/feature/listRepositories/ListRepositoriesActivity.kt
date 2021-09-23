package pessoto.android.mobile.challenge.listagithub.feature.listRepositories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pessoto.android.mobile.challenge.listagithub.R
import pessoto.android.mobile.challenge.listagithub.util.view.BaseActivity

class ListRepositoriesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_repositories)
    }
}