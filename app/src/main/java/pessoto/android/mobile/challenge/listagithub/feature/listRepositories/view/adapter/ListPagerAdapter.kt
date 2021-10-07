package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepository
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepositoryImpl
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.fragment.ListFragment

class ListPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val repository: ListRepositoriesRepository by lazy {
        ListRepositoriesRepositoryImpl()
    }

    private val listFragment = arrayOf(
        ListFragment.newInstance("kotlin", repository),
        ListFragment.newInstance("java", repository),
        ListFragment.newInstance("c#", repository),
        ListFragment.newInstance("python", repository)
    )

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return listFragment[position].language
    }

    override fun getCount(): Int {
        return listFragment.size
    }
}