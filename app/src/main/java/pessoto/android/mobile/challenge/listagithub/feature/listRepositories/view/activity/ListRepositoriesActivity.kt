package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import pessoto.android.mobile.challenge.listagithub.databinding.ActivityListRepositoriesBinding
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.dialog.ListRepositoriesDialog
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepository
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepositoryImpl
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.adapter.AdapterRepositories
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.viewModel.ListRepositoriesViewModel
import pessoto.android.mobile.challenge.listagithub.model.Items
import pessoto.android.mobile.challenge.listagithub.model.Result
import pessoto.android.mobile.challenge.listagithub.model.StateView
import pessoto.android.mobile.challenge.listagithub.util.extensions.smoothSnapToPosition
import pessoto.android.mobile.challenge.listagithub.util.view.BaseActivity
import pessoto.android.mobile.challenge.listagithub.util.view.components.EditTextSearch
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint( "NotifyDataSetChanged")
class ListRepositoriesActivity : BaseActivity() {

    private lateinit var binding: ActivityListRepositoriesBinding
    private val PREFS_NAME = "pessoto.android.mobile.challenge.listagithub.showDialog"
    private val PREF_PREFIX_KEY = "showDialog"
    private var listRepositoriesNotChanged = ArrayList<Items>()
    private var listRepositoriesChanged = ArrayList<Items>()
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
            showCardError(message = "Carregando lista de repositórios!\nAguarde por favor...", visibilityTryAgain = View.GONE)
        } else {
            binding.progressBar.visibility = View.VISIBLE
            binding.clError.visibility = View.GONE
        }
        binding.btnTryAgain.visibility = View.GONE
    }

    private fun stateDataLoaded(stateView: StateView.DataLoaded<Result>) {
        binding.progressBar.visibility = View.GONE
        binding.progressBarMessage.visibility = View.GONE
        binding.clError.visibility = View.GONE
        binding.btnTryAgain.visibility = View.GONE
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
        binding.progressBar.visibility = View.GONE
        binding.progressBarMessage.visibility = View.GONE
        binding.btnTryAgain.visibility = View.GONE

        next = true

        when (stateView.e) {
            is UnknownHostException -> {
                if (listRepositoriesNotChanged.isEmpty()) {
                    showCardError(
                        message = "Nenhum repositirório encontrado.\nVerifique sua conexão e tente novamente.",
                        visibilityProgressBar = View.GONE
                    )
                } else {
                    showSnackBar("Verifique sua conexão, por favor")
                }
            }
            else -> {
                if (listRepositoriesNotChanged.isEmpty()) {
                    showCardError(
                        "Ocorreu um erro inesperado.\nPor favor, tente novamente.",
                        View.GONE
                    )
                } else {
                    showSnackBar("Não foi possível atualizar a lista", Gravity.BOTTOM)
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

        verifySavedInstanceState(savedInstanceState)
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
            showCardError("Nenhum repositório encontrado!", View.GONE, View.GONE)
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
        binding.txtMessage.visibility = View.GONE
    }

    private fun verifySavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null && savedInstanceState.containsKey("repositories")
            && (savedInstanceState.getSerializable("repositories") as ArrayList<Items>).isNotEmpty()) {
            page = savedInstanceState.getInt("page")
            listRepositoriesNotChanged.addAll(savedInstanceState.getSerializable("repositories") as ArrayList<Items>)
            listRepositoriesChanged.addAll(listRepositoriesNotChanged)
            adapterRepositories.notifyDataSetChanged()
            binding.rcList.smoothSnapToPosition(savedInstanceState.getInt("toPosition"))
            binding.fabUp.visibility = savedInstanceState.getInt("fab")
            binding.rcList.visibility = View.VISIBLE
            binding.editTextSearch.visibility = View.VISIBLE

        } else {
            viewModel.getRepositories(language, page)
        }
    }

    private fun verifySharedPreferences() {
        if (getShowDialogPref()) {
            ListRepositoriesDialog.showDialog(this)
            saveShowDialogPref()
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

    private fun showSnackBar(message: String, position: Int = Gravity.TOP) {
        val snack: Snackbar = Snackbar.make(binding.rcList, message, Snackbar.LENGTH_SHORT)
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = position
        view.layoutParams = params
        snack.show()

        next = false
        Handler().postDelayed({ next = true }, 3000)
    }

    private fun showCardError(message: String, visibilityTryAgain: Int = View.VISIBLE, visibilityProgressBar: Int = View.VISIBLE) {
        binding.txtMessage.text = message
        binding.clError.visibility = View.VISIBLE
        binding.btnTryAgain.visibility = visibilityTryAgain
        binding.progressBarMessage.visibility = visibilityProgressBar
        binding.rcList.visibility = View.GONE
    }

}