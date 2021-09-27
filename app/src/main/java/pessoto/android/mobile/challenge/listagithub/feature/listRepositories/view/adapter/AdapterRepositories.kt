package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import pessoto.android.mobile.challenge.listagithub.databinding.AdapterRepositoriesBinding
import pessoto.android.mobile.challenge.listagithub.model.Item
import pessoto.android.mobile.challenge.listagithub.util.extensions.toFormat

class AdapterRepositories(
    var itemsList: ArrayList<Item>,
    private val onClick: (Item) -> Unit,
    private val onLongClick: (Item) -> Boolean,
    private val tryAgain: () -> Unit
) :
    RecyclerView.Adapter<AdapterRepositories.ViewHolder>() {

    inner class ViewHolder(
        private val binding: AdapterRepositoriesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            when {
                item.error.showLoading -> {
                    showLoading()
                }
                item.error.tryAgain -> {
                    showTryAgain(item)
                }
                else -> {
                    showRepository(item)
                }
            }
        }

        private fun showRepository(item: Item) {
            binding.cardView.visibility = View.VISIBLE
            binding.clLoading.visibility = View.GONE
            binding.clTryAgain.visibility = View.GONE

            binding.root.setOnClickListener {
                onClick(item)
            }

            binding.root.setOnLongClickListener {
                onLongClick(item)
            }

            binding.apply {
                txtNameRepository.text = item.name
                txtLogin.text = item.owner.login
                txtStars.text = item.stars.toFormat()
                txtFork.text = item.forks.toFormat()

                Picasso.get().load(item.owner.urlAvatar).into(imgAvatar, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                        imgAvatar.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {

                    }
                })
            }
        }

        private fun showTryAgain(item: Item) {
            binding.cardView.visibility = View.GONE
            binding.clLoading.visibility = View.GONE
            binding.clTryAgain.visibility = View.VISIBLE
            binding.txtMessage.text = item.error.tryAgainMessage
            binding.btnTryAgain.setOnClickListener {
                tryAgain()
            }
        }

        private fun showLoading() {
            binding.cardView.visibility = View.GONE
            binding.clTryAgain.visibility = View.GONE
            binding.clLoading.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterRepositoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}