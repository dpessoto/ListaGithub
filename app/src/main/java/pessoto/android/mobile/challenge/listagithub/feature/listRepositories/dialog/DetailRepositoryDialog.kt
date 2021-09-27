package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_detail_repository.view.*
import pessoto.android.mobile.challenge.listagithub.R
import pessoto.android.mobile.challenge.listagithub.Router
import pessoto.android.mobile.challenge.listagithub.model.Item
import pessoto.android.mobile.challenge.listagithub.util.extensions.toUnderline

@SuppressLint("ViewConstructor")
class DetailRepositoryDialog(context: Context, item: Item) : ConstraintLayout(context) {
    private lateinit var dialog: Dialog

    init {
        init(context, item)
    }

    private fun init(context: Context, item: Item) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.dialog_detail_repository, this)

        setComponents(item)
        setClicks(context, item)
    }

    private fun setClicks(context: Context, item: Item) {
        txtFullName.setOnClickListener {
            Router.instance.goToWeb(context, item.urlRepository)
            dialog.dismiss()
        }

        txtName.setOnClickListener {
            Router.instance.goToWeb(context, item.owner.urlOwner)
            dialog.dismiss()
        }
    }

    private fun setComponents(item: Item) {
        Picasso.get().load(item.owner.urlAvatar).into(imgAvatar)
        txtFullName.text = item.fullName
        txtFullName.toUnderline()
        txtName.text = item.owner.login
        txtName.toUnderline()
        txtStars.text = item.stars.toString()
        txtFork.text = item.forks.toString()
        txtDescription.text = item.description
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        dialog.dismiss()
    }

    companion object {
        @SuppressLint("ObsoleteSdkInt")
        fun showDialog(context: Activity, item: Item) {
            val dialog = Dialog(context)
            val content = DetailRepositoryDialog(context, item)
            content.dialog = dialog
            dialog.setContentView(content)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.setLayout(
                (getScreenWidth(context) * .9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setGravity(Gravity.CENTER)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.create()
            }
            dialog.show()
        }

        private fun getScreenWidth(activity: Activity): Int {
            val size = Point()
            activity.windowManager.defaultDisplay.getSize(size)
            return size.x
        }
    }


}