package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import pessoto.android.mobile.challenge.listagithub.databinding.ActivityListRepositoriesBinding
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepository
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepositoryImpl
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.adapter.AdapterRepositories
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.viewModel.ListRepositoriesViewModel
import pessoto.android.mobile.challenge.listagithub.model.Items
import pessoto.android.mobile.challenge.listagithub.model.Result
import pessoto.android.mobile.challenge.listagithub.model.StateView
import pessoto.android.mobile.challenge.listagithub.util.extensions.smoothSnapToPosition
import pessoto.android.mobile.challenge.listagithub.util.view.BaseActivity
import pessoto.android.mobile.challenge.listagithub.util.view.Dialogs
import pessoto.android.mobile.challenge.listagithub.util.view.components.EditTextSearch
import java.net.UnknownHostException

class ListRepositoriesActivity : BaseActivity() {

    private lateinit var binding: ActivityListRepositoriesBinding
    private var listRepositoriesNotChanged = ArrayList<Items>()
    private var listRepositoriesChanged = ArrayList<Items>()
    private var page = 1
    var showError = true
    var next = true
    var currentItems = 0
    var totalItems = 0
    var scrollOutItems = 0
    var language = "kotlin"

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
        AdapterRepositories(listRepositoriesChanged)
    }

    private val observer = Observer<StateView<Result>> { stateView ->
        when (stateView) {
            is StateView.Loading -> {
                if (listRepositoriesNotChanged.isEmpty()) {
                    binding.clError.visibility = View.VISIBLE
                    binding.progressBarMessage.visibility = View.VISIBLE
                    binding.txtMessage.text =
                        "Carregando lista de repositórios!\nAguarde por favor..."
                } else {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.clError.visibility = View.GONE
                }
                binding.btnTryAgain.visibility = View.GONE
            }
            is StateView.DataLoaded -> {
                binding.progressBar.visibility = View.GONE
                binding.progressBarMessage.visibility = View.GONE
                binding.clError.visibility = View.GONE
                binding.btnTryAgain.visibility = View.GONE
                binding.rcList.visibility = View.VISIBLE

                page++
                next = true
                listRepositoriesNotChanged.addAll(stateView.data.items)
                listRepositoriesChanged.addAll(stateView.data.items)
                adapterRepositories.notifyDataSetChanged()
            }
            is StateView.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.progressBarMessage.visibility = View.GONE
                binding.btnTryAgain.visibility = View.GONE

                next = true

                when (stateView.e) {
                    is UnknownHostException -> {
                        if (listRepositoriesNotChanged.isEmpty()) {
                            binding.txtMessage.text =
                                "Nenhum repostirório encontrado.\nVerifique sua conexão e tente novamente."
                            binding.clError.visibility = View.VISIBLE
                            binding.btnTryAgain.visibility = View.VISIBLE

                        } else if (showError) {
                            Snackbar.make(
                                binding.rcList,
                                "Verifique sua conexão, por favor",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            showError = false

                            Handler().postDelayed({
                                showError = true
                            }, 3000)
                        }
                    }
                    else -> {
                        if (listRepositoriesNotChanged.isEmpty()) {
                            binding.txtMessage.text =
                                "Ocorreu um erro inesperado.\nPor favor, tente novamente."
                            binding.clError.visibility = View.VISIBLE
                            binding.btnTryAgain.visibility = View.VISIBLE

                        } else if (showError) {
                            Snackbar.make(
                                binding.rcList,
                                "Não foi possível atualizar a lista",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            showError = false

                            Handler().postDelayed({
                                showError = true
                            }, 3000)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
                    viewModel.getRepositories(language, page)
                }
            }
        })

        binding.btnTryAgain.setOnClickListener {
            viewModel.getRepositories(language, page)
        }

        binding.fabUp.setOnClickListener {
            binding.rcList.smoothSnapToPosition(0)
        }

        binding.editTextSearch.addTextChangedListener =
            object : EditTextSearch.AddTextChangedListener {
                @SuppressLint("DefaultLocale")
                override fun textChanged(text: String) {
                   val textLowerCase = text.toLowerCase()
                    if (text.isNotEmpty()) {
                        next = false
                        listRepositoriesChanged.clear()
                        listRepositoriesChanged.apply {
                            addAll(listRepositoriesNotChanged.filter { items ->
                                val fullName = items.fullName.toLowerCase()
                                val login = items.owner.login.toLowerCase()
                                fullName.contains(textLowerCase) || login.contains(textLowerCase)
                            })
                        }
                    } else {
                        next = true
                        listRepositoriesChanged.clear()
                        listRepositoriesChanged.addAll(listRepositoriesNotChanged)
                    }
                    adapterRepositories.notifyDataSetChanged()
                }
            }

        viewModel.stateView.observe(this, observer)

        if (savedInstanceState != null && savedInstanceState.containsKey("repositories") && (savedInstanceState.getSerializable(
                "repositories"
            ) as ArrayList<Items>).isNotEmpty()
        ) {
            page = savedInstanceState.getInt("page")
            listRepositoriesNotChanged.addAll(savedInstanceState.getSerializable("repositories") as ArrayList<Items>)
            listRepositoriesChanged.addAll(listRepositoriesNotChanged)
            adapterRepositories.notifyDataSetChanged()
            binding.rcList.smoothSnapToPosition(savedInstanceState.getInt("toPosition"))
            binding.fabUp.visibility = savedInstanceState.getInt("fab")
            binding.rcList.visibility = View.VISIBLE
        } else {
            viewModel.getRepositories(language, page)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (listRepositoriesNotChanged.isNotEmpty()) {
            outState.putInt("toPosition", scrollOutItems)
            outState.putInt("page", page)
            outState.putSerializable("repositories", listRepositoriesNotChanged)
            outState.putInt("fab", binding.fabUp.visibility)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stateView.removeObserver(observer)
        Dialogs.cancelDialog()
    }

}