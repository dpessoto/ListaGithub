package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.view.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import pessoto.android.mobile.challenge.listagithub.databinding.AdapterRepositoriesBinding
import pessoto.android.mobile.challenge.listagithub.model.Items
import pessoto.android.mobile.challenge.listagithub.util.extensions.toFormat
import kotlin.math.max

class AdapterRepositories(
    var itemsList: ArrayList<Items>,
    private val onClick: (Items) -> Unit,
    private val onLongClick: (Items) -> Boolean

) :
    RecyclerView.Adapter<AdapterRepositories.ViewHolder>() {

    inner class ViewHolder(
        private val binding: AdapterRepositoriesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Items) {

            binding.root.setOnClickListener {
                onClick(item)
            }

            binding.root.setOnLongClickListener {
                onLongClick(item)
            }

            binding.apply {
                txtNameRepository.text = item.fullName
                txtLogin.text = item.owner.login
                txtStars.text = item.stars.toFormat()
                txtFork.text = item.forks.toFormat()

                Picasso.get().load(item.owner.urlAvatar).into(imgAvatar, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                        imgAvatar.visibility = View.VISIBLE
                        val imageBitmap = (imgAvatar.drawable as BitmapDrawable).bitmap
                        val imageDrawable =
                            RoundedBitmapDrawableFactory.create(imgAvatar.resources, imageBitmap)
                        imageDrawable.isCircular = true
                        imageDrawable.cornerRadius =
                            max(imageBitmap.width, imageBitmap.height) / 8.0f
                        imgAvatar.setImageDrawable(imageDrawable)
                    }

                    override fun onError(e: Exception?) {

                    }
                })
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
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}