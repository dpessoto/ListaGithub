package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pessoto.android.mobile.challenge.listagithub.databinding.AdapterRepositoriesBinding
import pessoto.android.mobile.challenge.listagithub.model.Items

class AdapterRepositories(var itemsList: ArrayList<Items>) :
    RecyclerView.Adapter<AdapterRepositories.ViewHolder>() {

    inner class ViewHolder(
        private val binding: AdapterRepositoriesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Items, position: Int) {

            binding.apply {
                txtFullName.text = item.fullName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterRepositoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}