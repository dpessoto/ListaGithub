package pessoto.android.mobile.challenge.listagithub.feature.listRepositories.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import pessoto.android.mobile.challenge.listagithub.R

class ListRepositoriesDialog(context: Context) : ConstraintLayout(context) {
    private lateinit var dialog: Dialog
    private lateinit var imgClose: ImageButton

    init {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.dialog_list_repositories, this)

        imgClose = findViewById(R.id.imgClose)
        imgClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    companion object {
        @SuppressLint("ObsoleteSdkInt")
        fun showDialog(context: Activity) {
            val dialog = Dialog(context)
            val content = ListRepositoriesDialog(context)
            content.dialog = dialog
            dialog.setContentView(content)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setGravity(Gravity.CENTER)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.create()
            }
            dialog.show()
        }
    }
}