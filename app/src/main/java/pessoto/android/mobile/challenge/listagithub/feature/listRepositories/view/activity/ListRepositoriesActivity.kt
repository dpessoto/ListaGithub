package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.activity

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import pessoto.android.mobile.challenge.listagithub.databinding.ActivityListRepositoriesBinding
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.dialog.ListRepositoriesDialog
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.adapter.ListPagerAdapter
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.fragment.ListFragmentView
import pessoto.android.mobile.challenge.listagithub.util.view.BaseActivity


class ListRepositoriesActivity : BaseActivity() {

    private lateinit var binding: ActivityListRepositoriesBinding
    private val PREFS_NAME = "pessoto.android.mobile.challenge.listagithub.showDialog"
    private val PREF_PREFIX_KEY = "showDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRepositoriesBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val listPagerAdapter = ListPagerAdapter(supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = listPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //TODO
            }

            override fun onPageSelected(position: Int) {
                val item = listPagerAdapter.getItem(position) as ListFragmentView
                item.hideKeyboard()
            }

            override fun onPageScrollStateChanged(state: Int) {
                //TODO
            }
        })
        verifySharedPreferences()
    }

    private fun verifySharedPreferences() {
        if (getShowDialogPref()) {
            ListRepositoriesDialog.showDialog(this)
            saveShowDialogPref()
        }
    }

    private fun saveShowDialogPref() {
        val prefs = this.getSharedPreferences(PREFS_NAME, 0).edit()
        prefs.putBoolean(PREF_PREFIX_KEY, false)
        prefs.apply()
    }

    private fun getShowDialogPref(): Boolean {
        val prefs = this.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getBoolean(PREF_PREFIX_KEY, true)
    }
}