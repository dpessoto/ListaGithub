package pessoto.android.mobile.challenge.listagithub.util.view.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import pessoto.android.mobile.challenge.listagithub.R
import pessoto.android.mobile.challenge.listagithub.util.extensions.hideKeyboard


class EditTextSearch : ConstraintLayout {

    interface AddTextChangedListener {
        fun textChanged(text: String)
    }

    private lateinit var imgClear: ImageButton
    private lateinit var txtCancel: TextView
    private lateinit var editSearch: TextInputEditText
    var addTextChangedListener: AddTextChangedListener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }


    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.sample_edit_text_search, this, true)
        imgClear = view.findViewById(R.id.imgClear)
        txtCancel = view.findViewById(R.id.txtCancel)
        editSearch = view.findViewById(R.id.editSearch)

        txtCancel.setOnClickListener {
            editSearch.text?.clear()
            editSearch.clearFocus()
            txtCancel.hideKeyboard()
        }

        imgClear.setOnClickListener {
            editSearch.text?.clear()
        }

        editSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(edit: Editable) {
                val text = edit.toString()
                if (text.isNotEmpty()) {
                    txtCancel.visibility = View.VISIBLE
                } else {
                    txtCancel.visibility = View.GONE
                }

                addTextChangedListener?.textChanged(text)
            }
        })
    }

}