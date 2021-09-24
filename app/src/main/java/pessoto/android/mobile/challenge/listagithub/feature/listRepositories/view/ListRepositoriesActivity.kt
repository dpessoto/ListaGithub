package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pessoto.android.mobile.challenge.listagithub.databinding.ActivityListRepositoriesBinding
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepository
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepositoryImpl
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.viewModel.ListRepositoriesViewModel
import pessoto.android.mobile.challenge.listagithub.model.Items
import pessoto.android.mobile.challenge.listagithub.model.Result
import pessoto.android.mobile.challenge.listagithub.model.StateView
import pessoto.android.mobile.challenge.listagithub.util.extensions.smoothSnapToPosition
import pessoto.android.mobile.challenge.listagithub.util.view.BaseActivity

class ListRepositoriesActivity : BaseActivity() {

    private lateinit var binding: ActivityListRepositoriesBinding
    private var listRepositories = ArrayList<Items>()
    private var page = 1
    var next = true
    var currentItems = 0
    var totalItems = 0
    var scrollOutItems = 0

    private val manager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    private val repository: ListRepositoriesRepository by lazy {
        ListRepositoriesRepositoryImpl()
    }

    private val viewModel: ListRepositoriesViewModel by lazy {
        ListRepositoriesViewModel(repository)
    }

    private val adapterRepositories by lazy {
        AdapterRepositories(listRepositories)
    }

    private val observer = Observer<StateView<Result>> { stateView ->
        when (stateView) {
            is StateView.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is StateView.DataLoaded -> {
                binding.progressBar.visibility = View.GONE
                page++
                next = true
                listRepositories.addAll(stateView.data.items)
                adapterRepositories.notifyDataSetChanged()
            }
            is StateView.Error -> {
                binding.progressBar.visibility = View.GONE
                next = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRepositoriesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rcList.apply {
            adapter = adapterRepositories
            layoutManager = manager
        }

        binding.rcList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager.childCount
                totalItems = manager.itemCount
                scrollOutItems = manager.findFirstVisibleItemPosition()

                if (scrollOutItems > 5) {
                    binding.fabUp.visibility = View.VISIBLE
                } else {
                    binding.fabUp.visibility = View.GONE
                }

                if ((currentItems + scrollOutItems > totalItems - 4) && next) {
                    next = false
                    viewModel.getRepositories("language:kotlin", page)
                }
            }
        })

        binding.fabUp.setOnClickListener {
            binding.rcList.smoothSnapToPosition(0)
        }

        viewModel.stateView.observe(this, observer)

        if (savedInstanceState != null && savedInstanceState.containsKey("scrollOutItems")) {
            page = savedInstanceState.getInt("page")
            listRepositories.addAll(savedInstanceState.getSerializable("repositories") as ArrayList<Items>)
            adapterRepositories.notifyDataSetChanged()
            binding.rcList.smoothSnapToPosition(savedInstanceState.getInt("toPosition"))
            binding.fabUp.visibility = savedInstanceState.getInt("fab")
        } else {
            viewModel.getRepositories("language:kotlin", page)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("toPosition", scrollOutItems)
        outState.putInt("page", page)
        outState.putSerializable("repositories", listRepositories)
        outState.putInt("fab", binding.fabUp.visibility)
    }

    override fun onStop() {
        super.onStop()
        viewModel.stateView.removeObserver(observer)
    }
}