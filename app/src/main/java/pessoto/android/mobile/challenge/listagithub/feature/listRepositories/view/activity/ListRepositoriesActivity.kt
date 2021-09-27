package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pessoto.android.mobile.challenge.listagithub.databinding.ActivityListRepositoriesBinding
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.dialog.ListRepositoriesDialog
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepository
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepositoryImpl
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.adapter.AdapterRepositories
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.viewModel.ListRepositoriesViewModel
import pessoto.android.mobile.challenge.listagithub.model.Item
import pessoto.android.mobile.challenge.listagithub.model.Result
import pessoto.android.mobile.challenge.listagithub.model.StateView
import pessoto.android.mobile.challenge.listagithub.util.extensions.smoothSnapToPosition
import pessoto.android.mobile.challenge.listagithub.util.view.BaseActivity
import pessoto.android.mobile.challenge.listagithub.util.view.components.EditTextSearch
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("NotifyDataSetChanged")
class ListRepositoriesActivity : BaseActivity() {

    private lateinit var binding: ActivityListRepositoriesBinding
    private val PREFS_NAME = "pessoto.android.mobile.challenge.listagithub.showDialog"
    private val PREF_PREFIX_KEY = "showDialog"
    private var listRepositoriesNotChanged = ArrayList<Item>()
    private var listRepositoriesChanged = ArrayList<Item>()
    private var page = 1
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
        AdapterRepositories(listRepositoriesChanged, { itemOnClick -> }, { itemLongClick ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(itemLongClick.urlRepository)))
            return@AdapterRepositories true
        }, {
            viewModel.getRepositories(language, page)
        })
    }

    private val observer = Observer<StateView<Result>> { stateView ->
        when (stateView) {
            is StateView.Loading -> {
                stateLoading()
            }
            is StateView.DataLoaded -> {
                stateDataLoaded(stateView)
            }
            is StateView.Error -> {
                stateError(stateView)
            }
        }
    }

    private fun stateLoading() {
        if (listRepositoriesNotChanged.isEmpty()) {
            showCardError(message = "Carregando lista de repositórios!\nAguarde por favor...", visibilityProgressBar = View.VISIBLE)
        } else {
            binding.clError.visibility = View.GONE
        }
        binding.btnTryAgain.visibility = View.GONE
    }

    private fun stateDataLoaded(stateView: StateView.DataLoaded<Result>) {
        binding.clError.visibility = View.GONE
        binding.rcList.visibility = View.VISIBLE
        binding.editTextSearch.visibility = View.VISIBLE

        page++
        next = true

        listRepositoriesNotChanged.addAll(stateView.data.items)
        listRepositoriesChanged.clear()
        listRepositoriesChanged.addAll(listRepositoriesNotChanged)
        adapterRepositories.notifyDataSetChanged()
    }

    private fun stateError(stateView: StateView.Error) {
        next = true

        when (stateView.e) {
            is UnknownHostException -> {
                if (listRepositoriesNotChanged.isEmpty()) {
                    showCardError(message = "Nenhum repositirório encontrado.\nVerifique sua conexão e tente novamente.", visibilityTryAgain = View.VISIBLE)
                } else {
                    showErrorInRecylerView("Verifique sua conexão, por favor")
                }
            }
            else -> {
                if (listRepositoriesNotChanged.isEmpty()) {
                    showCardError(message = "Ocorreu um erro inesperado.\nPor favor, tente novamente.", visibilityTryAgain = View.VISIBLE)
                } else {
                    showErrorInRecylerView("Não foi possível atualizar a lista")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRepositoriesBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        configRecyclerView()
        setClicks()

        binding.editTextSearch.addTextChangedListener = addTextChangedListener()

        viewModel.stateView.observe(this, observer)
        viewModel.getRepositories(language, page)
        verifySharedPreferences()
    }

    private fun configRecyclerView() {
        binding.rcList.apply {
            adapter = adapterRepositories
            layoutManager = manager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    currentItems = manager.childCount
                    totalItems = manager.itemCount
                    scrollOutItems = manager.findFirstVisibleItemPosition()

                    binding.fabUp.visibility = if (scrollOutItems > 5) View.VISIBLE else View.GONE

                    if ((currentItems + scrollOutItems > totalItems - 4) && next) {
                        next = false
                        adapterRepositories.addShowLoading()
                        viewModel.getRepositories(language, page)
                    }
                }
            })
        }
    }

    private fun setClicks() {
        binding.btnTryAgain.setOnClickListener {
            viewModel.getRepositories(language, page)
        }

        binding.fabUp.setOnClickListener {
            binding.rcList.smoothSnapToPosition(0)
        }
    }

    private fun addTextChangedListener() = object : EditTextSearch.AddTextChangedListener {
        override fun textChanged(text: String) {
            val textLowerCase = text.lowercase(Locale.getDefault())
            if (text.isNotEmpty()) {
                showListForSearch(textLowerCase)
            } else {
                next = true
                setVisibility()
                listRepositoriesChanged.clear()
                listRepositoriesChanged.addAll(listRepositoriesNotChanged)
            }
            adapterRepositories.notifyDataSetChanged()
        }
    }

    private fun showListForSearch(text: String) {
        next = false
        applyFilter(text)

        if (listRepositoriesChanged.isEmpty()) {
            showCardError(message = "Nenhum repositório encontrado!")
        } else {
            setVisibility()
        }
    }

    private fun applyFilter(text: String) {
        listRepositoriesChanged.clear()
        listRepositoriesChanged.apply {
            addAll(listRepositoriesNotChanged.filter { items ->
                val fullName = items.fullName.lowercase(Locale.getDefault())
                val login = items.owner.login.lowercase(Locale.getDefault())
                fullName.contains(text) || login.contains(text)
            })
        }
    }

    private fun setVisibility() {
        binding.rcList.visibility = View.VISIBLE
        binding.clError.visibility = View.GONE
    }

    private fun verifySharedPreferences() {
        if (getShowDialogPref()) {
            ListRepositoriesDialog.showDialog(this)
            saveShowDialogPref()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stateView.removeObserver(observer)
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

    private fun showErrorInRecylerView(message: String) {
        next = false
        adapterRepositories.showErrorInRecylerView(message)
    }

    private fun showCardError(message: String, visibilityTryAgain: Int = View.GONE, visibilityProgressBar: Int = View.GONE) {
        binding.txtMessage.text = message
        binding.clError.visibility = View.VISIBLE
        binding.btnTryAgain.visibility = visibilityTryAgain
        binding.progressBarMessage.visibility = visibilityProgressBar
        binding.rcList.visibility = View.GONE
    }

}