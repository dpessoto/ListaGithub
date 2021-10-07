package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pessoto.android.mobile.challenge.listagithub.Router
import pessoto.android.mobile.challenge.listagithub.databinding.FragmentListBinding
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.dialog.DetailRepositoryDialog
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.repository.ListRepositoriesRepository
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.adapter.AdapterRepositories
import pessoto.android.mobile.challenge.listagithub.feature.listRepositories.viewModel.ListRepositoriesViewModel
import pessoto.android.mobile.challenge.listagithub.model.Item
import pessoto.android.mobile.challenge.listagithub.model.Result
import pessoto.android.mobile.challenge.listagithub.model.StateView
import pessoto.android.mobile.challenge.listagithub.util.extensions.smoothSnapToPosition
import pessoto.android.mobile.challenge.listagithub.util.view.components.EditTextSearch
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("NotifyDataSetChanged")
class ListFragment(
    val language: String = "kotlin",
    private val repository: ListRepositoriesRepository,
) : Fragment(), ListFragmentView {
    lateinit var viewModel: ListRepositoriesViewModel
    private var _binding: FragmentListBinding? = null
    var listRepositoriesNotChanged = ArrayList<Item>()
    var listRepositoriesChanged = ArrayList<Item>()
    var page = 1
    var next = true
    var currentItems = 0
    var totalItems = 0
    var scrollOutItems = 0

    private val binding get() = _binding!!

    private val adapterRepositories by lazy {
        AdapterRepositories(listRepositoriesChanged, { itemOnClick ->
            activity?.let { DetailRepositoryDialog.showDialog(it, itemOnClick) }
        }, { itemLongClick ->
            context?.let { Router.instance.goToWeb(it, itemLongClick.urlRepository) }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ListRepositoriesViewModel(repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root = binding.root

        configRecyclerView()
        setClicks()
        binding.editTextSearch.addTextChangedListener = addTextChangedListener()

        viewModel.stateView.observe(viewLifecycleOwner, observer)

        if (listRepositoriesNotChanged.isNullOrEmpty())
            viewModel.getRepositories(language, page)

        return root
    }

    private fun configRecyclerView() {
        binding.rcList.apply {
            adapter = adapterRepositories
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    currentItems = linearLayoutManager.childCount
                    totalItems = linearLayoutManager.itemCount
                    scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition()

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

    private fun setClicks() {
        binding.fabUp.setOnClickListener {
            binding.rcList.smoothSnapToPosition(0)
        }

        binding.btnTryAgain.setOnClickListener {
            viewModel.getRepositories(language, page)
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

    companion object {
        @JvmStatic
        fun newInstance(
            language: String,
            repository: ListRepositoriesRepository
        ): ListFragment {
            return ListFragment(language, repository)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun hideKeyboard() {
        binding.editTextSearch.hideKeyboard()
    }

}