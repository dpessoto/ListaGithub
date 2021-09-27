package pessoto.android.mobile.challenge.listagithub.util.extensions

import android.graphics.Paint
import android.widget.TextView

fun TextView.toUnderline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}
