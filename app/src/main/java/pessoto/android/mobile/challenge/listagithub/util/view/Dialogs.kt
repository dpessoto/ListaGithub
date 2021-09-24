package pessoto.android.mobile.challenge.listagithub.util.view

import android.app.AlertDialog
import android.content.Context

interface DialogsCallback {
    fun callbackPositiveClick()
    fun callbackNegativeClick()
}

class Dialogs {

    companion object {
        private var alerta: AlertDialog? = null

        fun showDialog(
            context: Context,
            title: String,
            message: String,
            positiveButton: String,
            negativeButton: String,
            cancelable: Boolean,
            callback: DialogsCallback
        ) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                    positiveButton
                ) { _, _ ->
                    callback.callbackPositiveClick()
                    cancelDialog()
                }.setNegativeButton(
                    negativeButton
                ) { _, _ ->
                    callback.callbackNegativeClick()
                    cancelDialog()
                }.setCancelable(cancelable)

            if (alerta == null) {
                alerta = builder.create()
                alerta?.show()
            }
        }

        fun cancelDialog() {
            alerta?.dismiss()
            alerta = null
        }
    }
}